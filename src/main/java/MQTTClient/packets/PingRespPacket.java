package MQTTClient.packets;


import MQTTClient.data.PacketType;

public class PingRespPacket implements BrokerPacket {
    @Override
    public PacketType getPacketType() {
        return PacketType.PINGRESP;
    }

    @Override
    public String toString() {
        return getPacketType().name() + "{}";
    }

}
