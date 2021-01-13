package MQTTClient.extractors;


import MQTTClient.packets.BrokerPacket;
import MQTTClient.packets.PingRespPacket;

import java.nio.ByteBuffer;

public class PingRespExtractor implements MQTTBrokerExtractor {
    @Override
    public BrokerPacket extract(FixedHeader fixedHeader, ByteBuffer data) {
        if (fixedHeader.getRemainingLength() != 0)
            throw new IllegalArgumentException(String.format("Malformed Packet. Expected remaining length of %d, but got %d", 0, fixedHeader.getRemainingLength()));

        return new PingRespPacket();
    }
}
