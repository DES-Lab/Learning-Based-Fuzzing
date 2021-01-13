package MQTTClient.extractors;


import MQTTClient.data.VariableHeaderProperties;
import MQTTClient.packets.*;

import java.nio.ByteBuffer;

public class PubAckRecRelCompExtractor implements MQTTBrokerExtractor {
    @Override
    public BrokerPacket extract(FixedHeader fixedHeader, ByteBuffer data) {
        if (data.remaining() < 2 || fixedHeader.getRemainingLength() < 2) // at least packet identifier
            throw new IllegalArgumentException("Malformed Packet. Too short.");

        int packetIdentifier = (int) data.getShort() & 0xFFFF;
        byte reasonCode = 0x00;
        VariableHeaderProperties properties = new VariableHeaderProperties();

        if (fixedHeader.getRemainingLength() > 2)
            reasonCode = data.get();

        if (fixedHeader.getRemainingLength() > 3)
            properties = extractProperties(fixedHeader, data, 3, false);

        switch (fixedHeader.getPacketType()) {
            case PUBACK:
                return new PubAckPacket(packetIdentifier, reasonCode, properties);
            case PUBREC:
                return new PubRecPacket(packetIdentifier, reasonCode, properties);
            case PUBREL:
                return new PubRelPacket(packetIdentifier, reasonCode, properties);
            case PUBCOMP:
                return new PubCompPacket(packetIdentifier, reasonCode, properties);
            default:
                throw new IllegalArgumentException("Invalid extractor");
        }
    }
}
