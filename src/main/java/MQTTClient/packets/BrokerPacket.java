package MQTTClient.packets;

public interface BrokerPacket extends MQTTControlPacket {
    default boolean similar(BrokerPacket other) {
        return this.equals(other);
    }

}
