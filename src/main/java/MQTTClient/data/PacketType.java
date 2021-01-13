package MQTTClient.data;
import java.util.Arrays;

public enum PacketType {
    RESERVED((byte) 0),     // 0 = reserved so use it for other stuff
    WILL((byte) 0),         // 0 = reserved so use it for other stuff
    CONCLOSED((byte) 0),    // 0 = reserved so use it for other stuff
    CONNECT((byte) 1),
    CONNACK((byte) 2),
    PUBLISH((byte) 3),
    PUBACK((byte) 4),
    PUBREC((byte) 5),
    PUBREL((byte) 6),
    PUBCOMP((byte) 7),
    SUBSCRIBE((byte) 8),
    SUBACK((byte) 9),
    UNSUBSCRIBE((byte) 10),
    UNSUBACK((byte) 11),
    PINGREQ((byte) 12),
    PINGRESP((byte) 13),
    DISCONNECT((byte) 14),
    AUTH((byte) 15);

    private byte byteRep;

    PacketType(byte byteRep) {
        this.byteRep = byteRep;
    }

    public byte getByteRep() {
        return byteRep;
    }

    public static PacketType valueOf(byte byteRep) {
        return Arrays.stream(values())
                .filter(pt -> pt.getByteRep() != 0 && pt.getByteRep() == byteRep)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Value 0x%02X does not match a PacketType", byteRep & 0xFF)));
    }
}
