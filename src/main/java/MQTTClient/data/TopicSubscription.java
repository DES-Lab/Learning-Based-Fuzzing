package MQTTClient.data;

import MQTTClient.utils.MQTTUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class TopicSubscription {
    public enum RetainHandling {
        ALWAYS,
        NEW_SUBSCRIPTION,
        NEVER
    }

    private String topicFilter;
    private byte maxQoS; // TODO: QoS enum?
    private boolean noLocal; // do not send back to sender
    private boolean retainAsPublished; // forwarded messages keep the retain flag
    private RetainHandling retainHandling; // whether retained messages are sent

    public TopicSubscription(String topicFilter, byte maxQoS, boolean noLocal, boolean retainAsPublished, RetainHandling retainHandling) {
        this.topicFilter = topicFilter;
        this.maxQoS = maxQoS;
        this.noLocal = noLocal;
        this.retainAsPublished = retainAsPublished;
        this.retainHandling = retainHandling;
    }

    public String getTopicFilter() {
        return topicFilter;
    }

    public byte getMaxQoS() {
        return maxQoS;
    }

    public boolean getNoLocal() {
        return noLocal;
    }

    public boolean getRetainAsPublished() {
        return retainAsPublished;
    }

    public RetainHandling getRetainHandling() {
        return retainHandling;
    }

    public List<Byte> toBinary() {
        List<Byte> data = new ArrayList<>();

        data.addAll(MQTTUtils.encodeString(topicFilter));

        List<Boolean> subscriptionOptions = Arrays.asList((maxQoS & 0b01) != 0, (maxQoS & 0b10) != 0, noLocal,
                retainAsPublished, retainHandling == RetainHandling.NEW_SUBSCRIPTION,
                retainHandling == RetainHandling.NEVER, false, false);
        data.add((byte) IntStream.range(0, subscriptionOptions.size()).filter(subscriptionOptions::get).map(a -> (1 << a)).reduce((a, b) -> a | b).orElse(0));

        return data;
    }
}
