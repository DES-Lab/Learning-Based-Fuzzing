package MQTTClient.packets;

import MQTTClient.data.PacketType;
import MQTTClient.data.VariableHeaderProperties;

public class PubRecPacket extends PubAckPacket {
    public PubRecPacket(int packetIdentifier) {
        super(packetIdentifier);
    }

    public PubRecPacket(int packetIdentifier, byte reasonCode, VariableHeaderProperties properties) {
        super(packetIdentifier, reasonCode, properties);
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.PUBREC;
    }
}
