package MQTTClient.data;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class WillMessage extends MQTTMessage {
    private VariableHeaderProperties properties;

    public WillMessage(String topicName, String message, VariableHeaderProperties properties, byte qosLevel, boolean retain) {
        this(topicName, message.getBytes(StandardCharsets.UTF_8), properties, qosLevel, retain);
    }

    public WillMessage(String topicName, byte[] payload, VariableHeaderProperties properties, byte qosLevel, boolean retain) {
        super(topicName, payload, qosLevel, retain);

        if (properties != null && !properties.validForPacketType(PacketType.WILL)) {
            throw new IllegalArgumentException(String.format("Invalid properties for WILL message"));
        }

        this.properties = (properties == null) ? new VariableHeaderProperties() : properties;
    }

    public VariableHeaderProperties getProperties() {
        return properties;
    }

    public void setProperties(VariableHeaderProperties properties) {
        this.properties = properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        WillMessage that = (WillMessage) o;
        return Objects.equals(properties, that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), properties);
    }

    @Override
    public String toString() {
        return "WillMessage{" +
                "topicName='" + topicName + '\'' +
                ", payload='" + new String(payload, StandardCharsets.UTF_8) + '\'' +
                ", properties=" + properties +
                ", qosLevel=" + qosLevel +
                ", retain=" + retain +
                "} " + super.toString();
    }
}
