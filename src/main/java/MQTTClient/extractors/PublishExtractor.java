package MQTTClient.extractors;


import MQTTClient.data.MQTTMessage;
import MQTTClient.data.VariableHeaderProperties;
import MQTTClient.packets.BrokerPacket;
import MQTTClient.packets.PublishPacket;
import MQTTClient.utils.MQTTUtils;

import java.nio.ByteBuffer;

public class PublishExtractor implements MQTTBrokerExtractor {
    @Override
    public BrokerPacket extract(FixedHeader fixedHeader, ByteBuffer data) {
        boolean dupFlag = (fixedHeader.getFlags() & 0b1000) != 0;
        byte qosLevel = (byte) ((fixedHeader.getFlags() & 0b0110) >>> 1);
        boolean retain = (fixedHeader.getFlags() & 0b0001) != 0;

        if (qosLevel > 2) {
            throw new IllegalArgumentException("Malformed Packet. Invalid QoS.");
        }

        data.limit(data.position() + fixedHeader.getRemainingLength());

        String topicName = MQTTUtils.decodeString(data, true);

        int packetIdentifier = -1;
        if (qosLevel > 0) { // packet identifier only for qos 1 and 2
            packetIdentifier = (int) data.getShort() & 0xFFFF;
        }

        VariableHeaderProperties properties = extractProperties(fixedHeader, data, -1, true);

        byte[] payload = new byte[data.remaining()];
        data.get(payload);

        data.limit(data.capacity());
        return new PublishPacket(dupFlag, packetIdentifier, properties, new MQTTMessage(topicName, payload, qosLevel, retain));
    }
}
