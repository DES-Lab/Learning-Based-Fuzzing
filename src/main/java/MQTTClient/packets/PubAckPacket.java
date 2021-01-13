package MQTTClient.packets;

import MQTTClient.data.PacketType;
import MQTTClient.data.ReasonCode;
import MQTTClient.data.VariableHeaderProperties;
import MQTTClient.utils.MQTTUtils;

import java.util.ArrayList;
import java.util.List;

public class PubAckPacket implements ClientPacket, BrokerPacket {
    private int packetIdentifier;
    private ReasonCode reasonCode;
    private VariableHeaderProperties properties;

    public PubAckPacket(int packetIdentifier) {
        this(packetIdentifier, (byte) 0x00, null);
    }

    public PubAckPacket(int packetIdentifier, byte reasonCode, VariableHeaderProperties properties) {
        checkProperties(properties);

        this.packetIdentifier = packetIdentifier;
        this.properties = (properties == null) ? new VariableHeaderProperties() : properties;
        this.reasonCode = ReasonCode.valueOf(reasonCode, getPacketType());
    }

    public int getPacketIdentifier() {
        return packetIdentifier;
    }

    public ReasonCode getReasonCode() {
        return reasonCode;
    }

    public VariableHeaderProperties getProperties() {
        return properties;
    }

    @Override
    public List<Byte> toBinary() {
        List<Byte> data = new ArrayList<>();

        // Variable Header
        data.add((byte)((packetIdentifier >>> 8) & 0xFF));
        data.add((byte)(packetIdentifier & 0xFF));

        // can be omitted if no properties and success
        if (reasonCode != ReasonCode.SUCCESS || !properties.isEmpty()) {
            data.add(reasonCode.getValue());

            if (!properties.isEmpty()) // can be omitted
                data.addAll(properties.toBinary());
        }

        // Fixed Header at beginning
        data.addAll(0, MQTTUtils.encodeFixedHeader(getPacketType(), (byte) 0, data.size()));

        return data;
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.PUBACK;
    }

    @Override
    public String toString() {
        return getPacketType().name() + "{" +
                "packetIdentifier=" + packetIdentifier +
                ", reasonCode=" + reasonCode +
                ", properties=" + properties +
                '}';
    }

}
