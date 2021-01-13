package MQTTClient.packets;

import MQTTClient.data.PacketType;
import MQTTClient.data.VariableHeaderProperties;

import java.util.List;

public class UnsubAckPacket extends SubAckPacket {
    public UnsubAckPacket(int packetIdentifier, VariableHeaderProperties properties, List<Byte> reasonCodes) {
        super(packetIdentifier, properties, reasonCodes);
    }
    @Override
    public PacketType getPacketType() {
        return PacketType.UNSUBACK;
    }
}
