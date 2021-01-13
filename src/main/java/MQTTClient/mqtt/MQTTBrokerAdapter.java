package MQTTClient.mqtt;


import MQTTClient.data.MQTTBrokerAdapterConfig;
import MQTTClient.extractors.BrokerPacketExtractor;
import MQTTClient.packets.BrokerPacket;
import MQTTClient.packets.ClientPacket;
import MQTTClient.packets.ConnectionClosed;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

// Scala MQTT Broker Adapter by Martin Tappler ported to JAVA
public class MQTTBrokerAdapter {
    private MQTTBrokerAdapterConfig config;
    private Socket socket;
    private InputStream inStream;
    private OutputStream outStream;
    private Optional<String> closedReason;

    public MQTTBrokerAdapter(MQTTBrokerAdapterConfig config) {
        this.config = config;
        this.socket = null;
        inStream = null;
        outStream = null;
        closedReason = Optional.of("client_close");
    }

    public MQTTBrokerAdapterConfig getConfig() {
        return config;
    }

    public void connectSocket() throws IOException {
        socket = new Socket(config.internetAddress, config.port);
        socket.setTcpNoDelay(true);
        socket.setSoTimeout(config.timeout);

        inStream = socket.getInputStream();
        outStream = socket.getOutputStream();
        closedReason = Optional.empty();
    }

    public void closeSocket() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
            closedReason = Optional.of("client_close");
        }
    }

    public boolean isConnected() {
        return (socket != null && inStream != null && outStream != null &&
                socket.isConnected() && !closedReason.isPresent());
    }

    public List<BrokerPacket> readPackets() throws IOException {
        ByteBuffer dataBuffer = ByteBuffer.allocate(0);

        try {
            while (isConnected()) {
                byte[] binaryData = new byte[Math.max(inStream.available(), 1)];

                if (inStream.read(binaryData) == -1) {
                    closedReason = Optional.of("eof_stream");
                } else {
                    ByteBuffer newBuffer = ByteBuffer.allocate(dataBuffer.capacity() + binaryData.length);
                    newBuffer.put(dataBuffer);
                    newBuffer.put(binaryData);
                    newBuffer.rewind();

                    dataBuffer = newBuffer;
                }
            }
        } catch (SocketTimeoutException e) {
            // wait until timeout (which we know will eventually happen)
        } catch (SocketException e) {
            if (e.getMessage().contains("Connection reset")) {
                closedReason = Optional.of("conn_reset");
            } else {
                throw e;
            }
        }

        // extract packets
        List<BrokerPacket> packets = new ArrayList<>();
        while (dataBuffer.hasRemaining()) {
            packets.add(BrokerPacketExtractor.extract(dataBuffer));
        }
        packets.sort(Comparator.comparing(Object::toString));

        closedReason.ifPresent(s -> packets.add(new ConnectionClosed(s)));

        return packets;
    }

    public void sendPacket(ClientPacket packet) throws IOException {
        if (!isConnected())
            return;

//        System.out.println("Sending " + packet.getPacketType() + " packet");

        List<Byte> packetBytes = packet.toBinary();
        final byte[] packetRaw = new byte[packetBytes.size()];
        IntStream.range(0, packetBytes.size()).forEach(i -> packetRaw[i] = packetBytes.get(i));

        try {
            outStream.write(packetRaw);
        } catch (SocketException e) {
            if (e.getMessage().contains("Connection reset")) {
                closedReason = Optional.of("conn_reset");
            } else if (e.getMessage().contains("Broken pipe")) {
                closedReason = Optional.of("broken_pipe");
            } else {
                throw e;
            }
        }
    }

    public List<BrokerPacket> communicate(ClientPacket packet) throws IOException {
        sendPacket(packet);
        return readPackets();
    }
}
