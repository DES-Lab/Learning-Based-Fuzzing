package MQTTClient.extractors;

import MQTTClient.data.PacketType;

public class FixedHeader {
    private PacketType packetType;
    private byte flags;
    private int remainingLength;

    public FixedHeader(PacketType packetType, byte flags, int remainingLength) {
        this.packetType = packetType;
        this.flags = flags;
        this.remainingLength = remainingLength;
    }

    public PacketType getPacketType() {
        return packetType;
    }

    public void setPacketType(PacketType packetType) {
        this.packetType = packetType;
    }

    public byte getFlags() {
        return flags;
    }

    public void setFlags(byte flags) {
        this.flags = flags;
    }

    public int getRemainingLength() {
        return remainingLength;
    }

    public void setRemainingLength(int remainingLength) {
        this.remainingLength = remainingLength;
    }
}
