package MQTTClient.mqtt;


import MQTTClient.packets.BrokerPacket;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PacketFormatters {
    private Function<BrokerPacket, String> packetFormatter;
    private Function<List<String>, String> combiner;

    public PacketFormatters(Function<BrokerPacket, String> packetFormatter) {
        this(packetFormatter, seq -> seq.isEmpty() ? "Empty" : String.join("__", seq));
    }

    public PacketFormatters(Function<BrokerPacket, String> packetFormatter, Function<List<String>, String> combiner) {
        this.packetFormatter = packetFormatter;
        this.combiner = combiner;
    }

    public String apply(BrokerPacket packet) {
        return packetFormatter.apply(packet);
    }

    public String apply(List<BrokerPacket> packets) {
        return combiner.apply(packets.stream().map(packetFormatter).collect(Collectors.toList()));
    }
}
