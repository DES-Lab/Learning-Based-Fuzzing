package MQTTClient.extractors;



import MQTTClient.data.PacketType;
import MQTTClient.packets.BrokerPacket;
import MQTTClient.utils.MQTTUtils;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

// Packet-types from Broker:
// 2: CONNACK
// 3: PUBLISH
// 4: PUBACK (QoS1)
// (5, 6, 7: PUBREC, PUBREL, PUBCOMP ... QoS2)
// 9: SUBACK
// 11: UNSUBACK
// 13: PINGRESP
// 14: DISCONNECT
// (15: AUTH)
public abstract class BrokerPacketExtractor {
    private static final Map<PacketType, MQTTBrokerExtractor> extractors;

    static {
        extractors = new HashMap<>();
        extractors.put(PacketType.CONNACK, new ConnAckExtractor());
        extractors.put(PacketType.DISCONNECT, new DisconnectExtractor());
        extractors.put(PacketType.SUBACK, new SubUnsubAckExtractor());
        extractors.put(PacketType.UNSUBACK, new SubUnsubAckExtractor());
        extractors.put(PacketType.PUBLISH, new PublishExtractor());
        extractors.put(PacketType.PUBACK, new PubAckRecRelCompExtractor());
        extractors.put(PacketType.PUBREC, new PubAckRecRelCompExtractor());
        extractors.put(PacketType.PUBREL, new PubAckRecRelCompExtractor());
        extractors.put(PacketType.PUBCOMP, new PubAckRecRelCompExtractor());
        extractors.put(PacketType.PINGRESP, new PingRespExtractor());
    }

    protected static FixedHeader parseFixedHeader(ByteBuffer data) {
        // Byte 1: 0-3 = flags, 4-7 = type
        byte currentByte = data.get();
        PacketType packetType = PacketType.valueOf((byte) ((currentByte >>> 4) & 0x0F));
        byte flags = (byte) (currentByte & 0x0F);

        // Byte 2...: Remaining Length
        int remainingLength = MQTTUtils.decodeVariableByteInteger(data);

        // checks
        byte reservedFlags = (byte) ((packetType == PacketType.PUBREL) ? 0b0010 : 0);
        if (packetType != PacketType.PUBLISH && flags != reservedFlags) {
            String flagsString = String.format("%4s", Integer.toBinaryString(reservedFlags & 0x0F)).replace(' ', '0');
            throw new IllegalArgumentException(String.format("Malformed Packet. Flags must be %s.", flagsString));
        }

        if (data.remaining() < remainingLength)
            throw new IllegalArgumentException("Malformed Packet. Too short.");

        return new FixedHeader(packetType, flags, remainingLength);
    }

    public static BrokerPacket extract(ByteBuffer data) {
        FixedHeader fixedHeader = parseFixedHeader(data);
        MQTTBrokerExtractor extractor = extractors.get(fixedHeader.getPacketType());

        if (extractor == null) {
            throw new UnsupportedOperationException("Extractor for type " + fixedHeader.getPacketType() + " not implemented yet");
        } else {
            return extractor.extract(fixedHeader, data);
        }
    }
}
