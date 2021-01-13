package MQTTClient.utils;


import MQTTClient.data.PacketType;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface MQTTUtils {
    static List<Byte> encodeVariableByteInteger(int data) {
        // encoding algorithm from MQTT specification
        if (data < 0 || data > 268435455) {
            throw new IllegalArgumentException("Cannot convert integer to Variable Byte Integer");
        }

        List<Byte> encodedBytes = new ArrayList<>();

        int tmpData = data;
        do {
            byte encodedByte = (byte) (tmpData % 128);
            tmpData /= 128;

            if (tmpData > 0) {
                encodedByte |= 128;
            }

            encodedBytes.add(encodedByte);
        } while (tmpData > 0);

        assert (encodedBytes.size() <= 4);

        return encodedBytes;
    }

    static int decodeVariableByteInteger(ByteBuffer data) throws IllegalArgumentException {
        // decoding algorithm from MQTT specification
        if (!data.hasRemaining()) {
            throw new IllegalArgumentException("Malformed Variable Byte Integer");
        }

        int multiplier = 1;
        int value = 0;
        byte encodedByte;
        do {
            encodedByte = data.get();
            value += (encodedByte & 127) * multiplier;

            if (multiplier > 128 * 128 * 128 || ((encodedByte & 128) != 0 && !data.hasRemaining())) {
                throw new IllegalArgumentException("Malformed Variable Byte Integer");
            }

            multiplier *= 128;
        } while ((encodedByte & 128) != 0);

        return value;
    }

    static List<Byte> encodeString(String str) {
        if (str == null || str.length() > 0xFFFF) {
            throw new IllegalArgumentException("Cannot convert string");
        }

        List<Byte> encodedBytes = new ArrayList<>();
        byte[] strBytes = str.getBytes(StandardCharsets.UTF_8);
        // first 2 bytes = length
        encodedBytes.add((byte)((strBytes.length >>> 8) & 0xFF));
        encodedBytes.add((byte)(strBytes.length & 0xFF));

        // utf-8 string
        encodedBytes.addAll(IntStream.range(0, strBytes.length)
                .mapToObj(i -> strBytes[i])
                .collect(Collectors.toList()));

        return encodedBytes;
    }

    static String decodeString(ByteBuffer data, boolean prefixedLen) {
        int length = prefixedLen ? ((int) data.getShort()) & 0xFFFF : data.remaining();

        final byte[] dataBytes = new byte[length];
        data.get(dataBytes);

        return new String(dataBytes, StandardCharsets.UTF_8);
    }

    static List<Byte> encodeBinaryData(byte[] data) {
        if (data == null || data.length > 0xFFFF) {
            throw new IllegalArgumentException("Cannot convert binary data");
        }

        List<Byte> encodedBytes = new ArrayList<>();

        // first 2 bytes = length
        encodedBytes.add((byte)((data.length >>> 8) & 0xFF));
        encodedBytes.add((byte)(data.length & 0xFF));

        // data
        IntStream.range(0, data.length).forEach(i -> encodedBytes.add(data[i]));

        return encodedBytes;
    }

    static List<Byte> encodeFixedHeader(PacketType packetType, byte flags, int remainingLength) {
        List<Byte> data = new ArrayList<>();

        // type + flags
        byte firstByte = 0;
        firstByte |= ((packetType.getByteRep() & 0x0F) << 4);
        firstByte |= (flags & 0x0F);
        data.add(firstByte);

        // remaining length as Variable Byte Integer
        data.addAll(MQTTUtils.encodeVariableByteInteger(remainingLength));

        return data;
    }

    static void printBytes(List<Byte> data) {
        for (int i = 0; i < data.size(); i++) {
            String binaryString = String.format("%8s", Integer.toBinaryString(data.get(i) & 0xFF)).replace(' ', '0');
            System.out.println(String.format("%d: %s (%d)", i, binaryString, (data.get(i) & 0xFF)));
        }
    }

    static void printBytes(ByteBuffer data) {
        ByteBuffer buffer = data.asReadOnlyBuffer();
        while (buffer.hasRemaining()) {
            byte b = buffer.get();
            String binaryString = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            System.out.println(String.format("%d: %s (%d)", buffer.position() - 1, binaryString, (b & 0xFF)));
        }
    }

    static ByteBuffer listToByteBuffer(List<Byte> data) {
        ByteBuffer buffer = ByteBuffer.allocate(data.size());
        data.forEach(buffer::put);
        buffer.rewind();
        return buffer;
    }

    static byte boolsToByte(List<Boolean> bools) {
        if (bools.size() > 8)
            throw new IllegalArgumentException("Too many booleans");

        return (byte) IntStream.range(0, bools.size())
                .filter(bools::get)
                .map(a -> (1 << a))
                .reduce((a, b) -> a | b)
                .orElse(0);
    }
}
