package MQTTClient.packets;


import MQTTClient.data.MQTTMessage;
import MQTTClient.data.PacketType;
import MQTTClient.data.VariableHeaderProperties;
import MQTTClient.utils.MQTTUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PublishPacket implements ClientPacket, BrokerPacket {
    private boolean dupFlag;
    private int packetIdentifier;
    private VariableHeaderProperties properties;
    private MQTTMessage message;

    public PublishPacket(boolean dupFlag, int packetIdentifier, VariableHeaderProperties properties, MQTTMessage message) {
        checkProperties(properties);

        this.dupFlag = dupFlag;
        this.packetIdentifier = packetIdentifier;
        this.properties = (properties == null) ? new VariableHeaderProperties() : properties;
        this.message = message;
    }

    public boolean getDupFlag() {
        return dupFlag;
    }

    public int getPacketIdentifier() {
        return packetIdentifier;
    }

    public VariableHeaderProperties getProperties() {
        return properties;
    }

    public MQTTMessage getMessage() {
        return message;
    }

    @Override
    public List<Byte> toBinary() {
        List<Byte> data = new ArrayList<>();

        // Variable Header
        data.addAll(MQTTUtils.encodeString(message.getTopicName()));
        if (message.getQosLevel() > 0) {
            data.add((byte)((packetIdentifier >>> 8) & 0xFF));
            data.add((byte)(packetIdentifier & 0xFF));
        }
        data.addAll(properties.toBinary());

        // Payload
        List<Byte> payloadBytes = MQTTUtils.encodeBinaryData(message.getPayload());
        payloadBytes.subList(0, 2).clear(); // remove length
        data.addAll(payloadBytes);

        // Fixed Header at beginning
        List<Boolean> flags = Arrays.asList(message.getRetain(), (message.getQosLevel() & 0b01) != 0, (message.getQosLevel() & 0b10) != 0, dupFlag);
        data.addAll(0, MQTTUtils.encodeFixedHeader(getPacketType(), MQTTUtils.boolsToByte(flags), data.size()));

        return data;
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.PUBLISH;
    }

    @Override
    public boolean similar(BrokerPacket o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return this.message.equals(((PublishPacket) o).message);
    }


    @Override
    public String toString() {
        return "PublishPacket{" +
                "dupFlag=" + dupFlag +
                ", packetIdentifier=" + packetIdentifier +
                ", properties=" + properties +
                ", message=" + message +
                '}';
    }
}
