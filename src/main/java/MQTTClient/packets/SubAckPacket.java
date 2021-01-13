package MQTTClient.packets;


import MQTTClient.data.PacketType;
import MQTTClient.data.ReasonCode;
import MQTTClient.data.VariableHeaderProperties;

import java.util.List;
import java.util.stream.Collectors;

public class SubAckPacket implements BrokerPacket {
    private int packetIdentifier;
    private VariableHeaderProperties properties;
    private List<ReasonCode> reasonCodes;

    public SubAckPacket(int packetIdentifier, VariableHeaderProperties properties, List<Byte> reasonCodes) {
        checkProperties(properties);

        this.packetIdentifier = packetIdentifier;
        this.properties = (properties == null) ? new VariableHeaderProperties() : properties;
        this.reasonCodes = reasonCodes.stream().map(rc -> ReasonCode.valueOf(rc, getPacketType())).collect(Collectors.toList());
    }

    public int getPacketIdentifier() {
        return packetIdentifier;
    }

    public VariableHeaderProperties getProperties() {
        return properties;
    }

    public List<ReasonCode> getReasonCodes() {
        return reasonCodes;
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.SUBACK;
    }

    @Override
    public String toString() {
        return getPacketType().name() + "{" +
                "packetIdentifier=" + packetIdentifier +
                ", properties=" + properties +
                ", reasonCodes=" + reasonCodes +
                '}';
    }

}
