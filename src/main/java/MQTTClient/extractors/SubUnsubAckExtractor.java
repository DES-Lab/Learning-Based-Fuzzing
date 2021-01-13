package MQTTClient.extractors;


import MQTTClient.data.VariableHeaderProperties;
import MQTTClient.packets.BrokerPacket;
import MQTTClient.packets.SubAckPacket;
import MQTTClient.packets.UnsubAckPacket;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class SubUnsubAckExtractor implements MQTTBrokerExtractor {
    @Override
    public BrokerPacket extract(FixedHeader fixedHeader, ByteBuffer data) {
        if (data.remaining() < 4 || fixedHeader.getRemainingLength() < 4) // at least packet id + property length (0) + 1 reason code
            throw new IllegalArgumentException("Malformed Packet. Too short.");

        data.limit(data.position() + fixedHeader.getRemainingLength());

        int packetIdentifier = (int) data.getShort() & 0xFFFF;
        VariableHeaderProperties properties = extractProperties(fixedHeader, data, -1, true);

        List<Byte> reasonCodes = new ArrayList<>();
        while (data.hasRemaining()) {
            reasonCodes.add(data.get());
        }

        data.limit(data.capacity());

        switch (fixedHeader.getPacketType()) {
            case SUBACK:
                return new SubAckPacket(packetIdentifier, properties, reasonCodes);
            case UNSUBACK:
                return new UnsubAckPacket(packetIdentifier, properties, reasonCodes);
            default:
                throw new IllegalArgumentException("Invalid extractor");
        }
    }
}
