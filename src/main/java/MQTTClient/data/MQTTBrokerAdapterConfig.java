package MQTTClient.data;

import java.net.InetAddress;
import java.net.UnknownHostException;

// Scala MQTT Broker Adapter Config by Martin Tappler ported to JAVA
public class MQTTBrokerAdapterConfig {
    public InetAddress internetAddress;
    public int port = 1883;
    public int timeout = 25;
    public boolean renewSession = true; // TODO
    public boolean startSessionOnFirstConnect = true;
    public boolean autoAckQoS1 = true;

    public MQTTBrokerAdapterConfig() throws UnknownHostException {
        internetAddress = InetAddress.getLocalHost();
    }

    public MQTTBrokerAdapterConfig(InetAddress internetAddress, int port, int timeout, boolean renewSession, boolean startSessionOnFirstConnect, boolean autoAckQoS1) {
        this.internetAddress = internetAddress;
        this.port = port;
        this.timeout = timeout;
        this.renewSession = renewSession;
        this.startSessionOnFirstConnect = startSessionOnFirstConnect;
        this.autoAckQoS1 = autoAckQoS1;
    }
}
