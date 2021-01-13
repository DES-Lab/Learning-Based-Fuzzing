package MQTTClient.packets;


import MQTTClient.data.PacketType;

public class PingReqPacket implements ClientPacket {
    @Override
    public PacketType getPacketType() {
        return PacketType.PINGREQ;
    }
}
