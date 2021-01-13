package MQTTClient.data;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

public class MQTTMessage {
    protected String topicName;
    protected byte[] payload;
    protected byte qosLevel;
    protected boolean retain;

    public MQTTMessage(String topicName, String message, byte qosLevel, boolean retain) {
        this(topicName, message.getBytes(StandardCharsets.UTF_8), qosLevel, retain);
    }

    public MQTTMessage(String topicName, byte[] payload, byte qosLevel, boolean retain) {
        this.topicName = topicName;
        this.payload = payload;
        this.qosLevel = qosLevel;
        this.retain = retain;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public byte getQosLevel() {
        return qosLevel;
    }

    public void setQosLevel(byte qosLevel) {
        this.qosLevel = qosLevel;
    }

    public boolean getRetain() {
        return retain;
    }

    public void setRetain(boolean retain) {
        this.retain = retain;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MQTTMessage that = (MQTTMessage) o;
        return qosLevel == that.qosLevel &&
                retain == that.retain &&
                Objects.equals(topicName, that.topicName) &&
                Arrays.equals(payload, that.payload);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(topicName, qosLevel, retain);
        result = 31 * result + Arrays.hashCode(payload);
        return result;
    }

    @Override
    public String toString() {
        return "MQTTMessage{" +
                "topicName='" + topicName + '\'' +
                ", payload='" + new String(payload, StandardCharsets.UTF_8) + '\'' +
                ", qosLevel=" + qosLevel +
                ", retain=" + retain +
                '}';
    }
}
