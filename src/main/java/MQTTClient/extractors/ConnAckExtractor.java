package MQTTClient.extractors;

import MQTTClient.data.VariableHeaderProperties;
import MQTTClient.packets.BrokerPacket;
import MQTTClient.packets.ConnAckPacket;

import java.nio.ByteBuffer;

public class ConnAckExtractor implements MQTTBrokerExtractor {
    @Override
    public BrokerPacket extract(FixedHeader fixedHeader, ByteBuffer data) {
        if (data.remaining() < 3 || fixedHeader.getRemainingLength() < 3) // Connect Acknowledge Flags, Connect Reason Code, and Properties (at least length)
            throw new IllegalArgumentException("Malformed Packet. Too short.");

        byte acknowledgeFlags = data.get();
        byte reasonCode = data.get();

        if ((acknowledgeFlags & 0b11111110) != 0) {
            throw new IllegalArgumentException("Malformed Packet. Bits 7-1 of Connect Acknowledge Flags must be 0");
        }

        VariableHeaderProperties properties = extractProperties(fixedHeader, data, 2, false);

        return new ConnAckPacket((acknowledgeFlags & 0x01) != 0, reasonCode, properties);
    }
}
