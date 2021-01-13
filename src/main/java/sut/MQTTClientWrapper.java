package sut;



import MQTTClient.data.MQTTBrokerAdapterConfig;
import MQTTClient.data.MQTTMessage;
import MQTTClient.data.TopicSubscription;
import MQTTClient.data.WillMessage;
import MQTTClient.mqtt.MQTTClient;
import MQTTClient.mqtt.PacketFormatters;
import MQTTClient.packets.BrokerPacket;
import MQTTClient.packets.ConnectionClosed;
import MQTTClient.packets.PublishPacket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class MQTTClientWrapper implements SUTWrapper {
    private InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
    private Integer port = 1883;
    private Integer timeout = 200;
    private Boolean renewSession = true; // todo change back
    private Boolean startSessionOnFirstConnect = true;
    private Boolean autoAckQoS1 = true;
    private MQTTBrokerAdapterConfig brokerConfig = new MQTTBrokerAdapterConfig(inetAddress, port, timeout, renewSession, startSessionOnFirstConnect, autoAckQoS1);
    private PacketFormatters packetFormatters;
    private MQTTClient client;
    String id;
    String userName;

    private Function<BrokerPacket, String> formatter = (BrokerPacket packet) -> {
        switch (packet.getPacketType()) {
//            case PUBLISH:
//                MQTTMessage msg = ((PublishPacket) packet).getMessage();
//                return ((PublishPacket) packet).getMessage().toString();
//            case CONCLOSED:
//                return "CONCLOSED_" + ((ConnectionClosed) packet).getReason();

            default:
                return packet.getPacketType().toString();
        }
    };

    public MQTTClientWrapper(String id, String username) throws UnknownHostException {
        packetFormatters = new PacketFormatters(formatter);
        client = new MQTTClient(id, username, null, brokerConfig, packetFormatters, 2);
        this.id = id;
        this.userName = username;
    }

    public MQTTClientWrapper(String id, String username, MQTTBrokerAdapterConfig brokerConfig) throws UnknownHostException {
        packetFormatters = new PacketFormatters(formatter);
        client = new MQTTClient(id, username, null, brokerConfig, packetFormatters, 2);
        this.id = id;
        this.userName = username;
    }

    @Override
    public void pre() {
        //client = new MQTTClient(id, userName, null, brokerConfig, packetFormatters, 2);
    }

    @Override
    public void post() throws IOException {
        client.resetAndDisconnect();
        client.closeTCPSession();
    }

    @Override
    public Class getDeclaringClass() {
        return this.getClass();
    }

    public String readIncoming() throws IOException {
        return client.readIncoming();
    }

    public String connect() throws IOException {
        client.connectToBroker(true); // hmmm
        return readIncoming();
    }

    public String connectWithWill(String topic, String msg) throws  IOException{
        client.connectToBroker(false, new WillMessage(topic, msg, null, (byte) 1, true)); // TODO CHanged that to false, does not seem to make diff, changed back
        return readIncoming();
    }

    public String disconnect() throws IOException {
        client.disconnectFromBroker();
        return readIncoming();
    }

    public String subscribe(String topicName) throws IOException {
        TopicSubscription topic = new TopicSubscription(topicName, (byte) 1, false, false, TopicSubscription.RetainHandling.ALWAYS);
        List<TopicSubscription> topicList = new ArrayList<>();
        topicList.add(topic);
        client.subscribe(topicList);
        return readIncoming();
    }

    public String unsubscribe(String topic) throws IOException {
        List<String> list = new ArrayList<>();
        list.add(topic);
        client.unsubscribe(list);
        return readIncoming();
    }

    public String publish(String topicName, String msg) throws IOException {
        MQTTMessage mqttMessage = new MQTTMessage(topicName, msg, (byte) 1, false); // TODO retain messages?
        client.publish(mqttMessage, true);
        return readIncoming();
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof MQTTClientWrapper) {
            return !this.id.equals(((MQTTClientWrapper) o).id);
        }
        return false;
    }

    public void reset() throws IOException {
        client.resetAndDisconnect();
        client.closeTCPSession();
    }

    public String getName(){
        return this.userName;
    }
}