package MQTTClient.extractors;

import MQTTClient.data.VariableHeaderProperties;
import MQTTClient.packets.BrokerPacket;
import MQTTClient.packets.DisconnectPacket;

import java.nio.ByteBuffer;

public class DisconnectExtractor implements MQTTBrokerExtractor {
    @Override
    public BrokerPacket extract(FixedHeader fixedHeader, ByteBuffer data) {
        byte reasonCode = 0x00;
        VariableHeaderProperties properties = new VariableHeaderProperties();

        if (fixedHeader.getRemainingLength() > 0)
            reasonCode = data.get();

        if (fixedHeader.getRemainingLength() > 1)
            properties = extractProperties(fixedHeader, data, 1, false);

        return new DisconnectPacket(reasonCode, properties);
    }
}
