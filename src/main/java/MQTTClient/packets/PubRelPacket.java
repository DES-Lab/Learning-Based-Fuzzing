package MQTTClient.packets;

import MQTTClient.data.PacketType;
import MQTTClient.data.VariableHeaderProperties;
import MQTTClient.utils.MQTTUtils;

import java.util.List;

public class PubRelPacket extends PubAckPacket {
    public PubRelPacket(int packetIdentifier) {
        super(packetIdentifier);
    }

    public PubRelPacket(int packetIdentifier, byte reasonCode, VariableHeaderProperties properties) {
        super(packetIdentifier, reasonCode, properties);
    }

    @Override
    public List<Byte> toBinary() {
        // replace flags
        List<Byte> data = super.toBinary();
        data.set(0, MQTTUtils.encodeFixedHeader(getPacketType(), ((byte) 0b0010), 0).get(0));

        return data;
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.PUBREL;
    }
}
