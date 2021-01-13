package MQTTClient.packets;


import MQTTClient.data.PacketType;
import MQTTClient.data.VariableHeaderProperties;
import MQTTClient.utils.MQTTUtils;

import java.util.ArrayList;
import java.util.List;

public class UnsubscribePacket implements ClientPacket {
    private int packetIdentifier;
    private VariableHeaderProperties properties;
    private List<String> topicFilters;

    public UnsubscribePacket(int packetIdentifier, VariableHeaderProperties properties, List<String> topicFilters) {
        checkProperties(properties);

        this.packetIdentifier = packetIdentifier;
        this.properties = (properties == null) ? new VariableHeaderProperties() : properties;
        this.topicFilters =  (topicFilters == null) ? new ArrayList<>() : topicFilters;

    }

    @Override
    public List<Byte> toBinary() {
        List<Byte> data = new ArrayList<>();

        // Variable Header
        data.add((byte)((packetIdentifier >>> 8) & 0xFF));
        data.add((byte)(packetIdentifier & 0xFF));
        data.addAll(properties.toBinary());

        // Payload
        topicFilters.forEach(topic -> data.addAll(MQTTUtils.encodeString(topic)));

        // Fixed Header at beginning
        data.addAll(0, MQTTUtils.encodeFixedHeader(getPacketType(), (byte) 0b0010, data.size()));

        return data;
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.UNSUBSCRIBE;
    }
}
