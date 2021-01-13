package MQTTClient.packets;


import MQTTClient.data.PacketType;
import MQTTClient.data.ReasonCode;
import MQTTClient.data.VariableHeaderProperties;

public class ConnAckPacket implements BrokerPacket {
    private boolean sessionPresent;
    private ReasonCode reasonCode;
    private VariableHeaderProperties properties;

    public ConnAckPacket(boolean sessionPresent, byte reasonCode, VariableHeaderProperties properties) {
        checkProperties(properties);

        this.sessionPresent = sessionPresent;
        this.reasonCode = ReasonCode.valueOf(reasonCode, getPacketType());
        this.properties = (properties == null) ? new VariableHeaderProperties() : properties;
    }

    public boolean getSessionPresent() {
        return sessionPresent;
    }

    public ReasonCode getReasonCode() {
        return reasonCode;
    }

    public VariableHeaderProperties getProperties() {
        return properties;
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.CONNACK;
    }

    @Override
    public String toString() {
        return getPacketType().name() + "{" +
                "sessionPresent=" + sessionPresent +
                ", reasonCode=" + reasonCode +
                ", properties=" + properties +
                '}';
    }

}
