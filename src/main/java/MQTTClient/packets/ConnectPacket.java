package MQTTClient.packets;


import MQTTClient.data.PacketType;
import MQTTClient.data.VariableHeaderProperties;
import MQTTClient.data.WillMessage;
import MQTTClient.utils.MQTTUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConnectPacket implements ClientPacket {
    private String clientIdentifier;
    private boolean cleanStart;
    private int keepAlive;
    private VariableHeaderProperties properties;
    private String username;
    private byte[] password;
    private WillMessage willMessage;

    public ConnectPacket(String clientIdentifier, boolean cleanStart) {
        this(clientIdentifier, cleanStart, 0, new VariableHeaderProperties(), null, null, null);
    }

    public ConnectPacket(String clientIdentifier, boolean cleanStart, int keepAlive, VariableHeaderProperties properties,
                         String username, byte[] password, WillMessage willMessage) {
        checkProperties(properties);

        this.clientIdentifier = clientIdentifier;
        this.cleanStart = cleanStart;
        this.keepAlive = keepAlive;
        this.properties = (properties == null) ? new VariableHeaderProperties() : properties;
        this.username = username;
        this.password = password;
        this.willMessage = willMessage;
    }

    @Override
    public List<Byte> toBinary() {
        List<Byte> data = new ArrayList<>();

        // Variable Header
        data.addAll(MQTTUtils.encodeString("MQTT")); // Protocol Name
        data.add((byte) 5); // Protocol Version (5.0)

        List<Boolean> connectFlags = Arrays.asList(false, cleanStart, willMessage != null, false,
                false, false, password != null, username != null);
        if (willMessage != null) {
            connectFlags.set(3, (willMessage.getQosLevel() & 0b01) != 0);
            connectFlags.set(4, (willMessage.getQosLevel() & 0b10) != 0);
            connectFlags.set(5, willMessage.getRetain());
        }
        data.add(MQTTUtils.boolsToByte(connectFlags));

        data.add((byte)((keepAlive >>> 8) & 0xFF));
        data.add((byte)(keepAlive & 0xFF));

        data.addAll(properties.toBinary());

        // Payload (all but client identifier can be omitted)
        data.addAll(MQTTUtils.encodeString(clientIdentifier));

        if (willMessage != null) {
            data.addAll(willMessage.getProperties().toBinary());
            data.addAll(MQTTUtils.encodeString(willMessage.getTopicName()));
            data.addAll(MQTTUtils.encodeBinaryData(willMessage.getPayload()));
        }

        if (username != null)
            data.addAll(MQTTUtils.encodeString(username));

        if (password != null)
            data.addAll(MQTTUtils.encodeBinaryData(password));

        // Fixed Header at beginning
        data.addAll(0, MQTTUtils.encodeFixedHeader(getPacketType(), (byte) 0, data.size()));

        return data;
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.CONNECT;
    }
}
