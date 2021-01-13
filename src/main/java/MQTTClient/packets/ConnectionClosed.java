package MQTTClient.packets;


import MQTTClient.data.PacketType;

public class ConnectionClosed implements BrokerPacket{
    private String reason;

    public ConnectionClosed(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.CONCLOSED;
    }

    @Override
    public String toString() {
        return "ConnectionClosed{" +
                "reason='" + reason + '\'' +
                '}';
    }

}
