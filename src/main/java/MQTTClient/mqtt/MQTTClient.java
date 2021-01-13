package MQTTClient.mqtt;

import MQTTClient.data.*;
import MQTTClient.packets.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// Scala MQTT Client by Martin Tappler ported to JAVA
// NOTE: Properties in MQTT 5.0 are currently not used (except session expiry interval)
public class MQTTClient {
    private final AtomicInteger lastPacketId = new AtomicInteger(1);
    private final IntSupplier packetIdSupplier = () -> lastPacketId.getAndUpdate(id -> (id % 0xFFFF) + 1); // 2 Byte, non-zero

    private String identifier;
    private String username;
    private WillMessage will;
    private MQTTBrokerAdapterConfig adapterConfig;
    private PacketFormatters packetFormatters;

    private MQTTBrokerAdapter adapter;
    private Set<String> retainedMessageTopics;
    private Map<String, Byte> subscribedTopics; // Topic -> QoS
    private Map<Integer, List<String>> subscriptionRequests; // packetId -> Topics
    private List<Integer> outstandingQoS1Acks;
    private boolean hasBeenConnected;
    private int maxSimilarPacketsInResponse;

    public MQTTClient(String identifier, String username, WillMessage will,
                      MQTTBrokerAdapterConfig adapterConfig, PacketFormatters packetFormatters,
                      int maxSimilarPacketsInResponse) {
        this.identifier = identifier;
        this.username = username;
        this.will = will;
        this.adapterConfig = adapterConfig;
        this.packetFormatters = packetFormatters;
        this.adapter = new MQTTBrokerAdapter(adapterConfig);
        this.retainedMessageTopics = new HashSet<>();
        this.subscribedTopics = new HashMap<>();
        this.subscriptionRequests = new HashMap<>();
        this.outstandingQoS1Acks = new ArrayList<>();
        this.hasBeenConnected = false;
        this.maxSimilarPacketsInResponse = maxSimilarPacketsInResponse;
    }

    public void startTCPSession() throws IOException {
        if (hasBeenConnected || !adapterConfig.startSessionOnFirstConnect)
            adapter.connectSocket();
    }

    public void closeTCPSession() throws IOException {
        adapter.closeSocket();
    }

    public void connectToBroker(boolean cleanStart) throws IOException {
        connectToBroker(cleanStart, null);
    }

    public void connectToBroker(boolean cleanStart, WillMessage will) throws IOException {
        if (!hasBeenConnected && adapterConfig.startSessionOnFirstConnect || adapterConfig.renewSession && !adapter.isConnected()) {
            hasBeenConnected = true;
            adapter.connectSocket();
        }

        VariableHeaderProperties properties = new VariableHeaderProperties();
        //properties.setSessionExpiryInterval(0); // 0xFFFFFFFF (UINT_MAX) = the session does not expire.
        properties.setSessionExpiryInterval(0xFFFFFFFF); // 0xFFFFFFFF (UINT_MAX) = the session does not expire.
        ConnectPacket packet = new ConnectPacket(identifier, cleanStart, 0, properties, username, null, will);
        adapter.sendPacket(packet);

        if (will != null && will.getRetain())
            retainedMessageTopics.add(will.getTopicName());

        if (cleanStart)
            subscribedTopics.clear();
    }

    public void disconnectFromBroker() throws IOException {
        DisconnectPacket packet = new DisconnectPacket();
        adapter.sendPacket(packet);
    }

    public void publish(MQTTMessage message, boolean dup) throws IOException {
        if (message.getQosLevel() > 1)
            throw new IllegalArgumentException("Client only supports QoS 0 or 1");

        PublishPacket packet = new PublishPacket(dup, message.getQosLevel() > 0 ? packetIdSupplier.getAsInt() : -1, null, message);
        adapter.sendPacket(packet);

        if (message.getRetain())
            retainedMessageTopics.add(message.getTopicName());
    }

    public void subscribe(List<TopicSubscription> subscriptions) throws IOException {
        if (subscriptions.stream().anyMatch(sub -> sub.getMaxQoS() > 1))
            throw new IllegalArgumentException("Client only supports QoS 0 or 1");

        int packetIdentifier = packetIdSupplier.getAsInt();
        SubscribePacket packet = new SubscribePacket(packetIdentifier, null, subscriptions);
        adapter.sendPacket(packet);

        subscriptionRequests.put(packetIdentifier, subscriptions.stream().map(TopicSubscription::getTopicFilter).collect(Collectors.toList()));
    }

    public void unsubscribe(List<String> topicFilters) throws IOException {
        UnsubscribePacket packet = new UnsubscribePacket(packetIdSupplier.getAsInt(), null, topicFilters);
        adapter.sendPacket(packet);

        subscribedTopics.keySet().removeAll(topicFilters);
    }

    public void ping() throws IOException {
        PingReqPacket packet = new PingReqPacket();
        adapter.sendPacket(packet);
    }

    public void processSubscriptionRequests(List<BrokerPacket> packets) {
        for (BrokerPacket brokerPacket : packets) {
            if (brokerPacket.getPacketType() != PacketType.SUBACK)
                continue;

            SubAckPacket packet = (SubAckPacket) brokerPacket;
            List<String> topics = subscriptionRequests.remove(packet.getPacketIdentifier());
            List<ReasonCode> reasonCodes = packet.getReasonCodes();

            IntStream.range(0, Math.min(topics == null ? 0 : topics.size(), reasonCodes.size()))
                    .filter(i -> reasonCodes.get(i).getValue() <= 2) // allow only accepted subscriptions
                    .forEach(i -> subscribedTopics.put(topics.get(i), reasonCodes.get(i).getValue()));
        }
    }

    public List<BrokerPacket> filterSimilarPackets(List<BrokerPacket> packets) {
        List<List<BrokerPacket>> allSimilarPackets = new ArrayList<>();
        for (BrokerPacket packet : packets) {
            List<BrokerPacket> similarPackets = allSimilarPackets.stream()
                    .filter(l -> !l.isEmpty() && l.get(0).similar(packet))
                    .findFirst()
                    .orElseGet(() -> {
                        allSimilarPackets.add(new ArrayList<>());
                        return allSimilarPackets.get(allSimilarPackets.size() - 1);
                    });

            if (similarPackets.size() < maxSimilarPacketsInResponse)
                similarPackets.add(packet);
        }

        return allSimilarPackets.stream().flatMap(List::stream).collect(Collectors.toList());
    }

    public void acknowledgeQos1Subscriptions(List<BrokerPacket> packets) throws IOException {
        for (BrokerPacket brokerPacket : packets) {
            if (brokerPacket.getPacketType() != PacketType.PUBLISH || ((PublishPacket) brokerPacket).getMessage().getQosLevel() != 1)
                continue;

            PublishPacket packet = (PublishPacket) brokerPacket;
            if (adapterConfig.autoAckQoS1) {
                adapter.sendPacket(new PubAckPacket(packet.getPacketIdentifier(), ReasonCode.SUCCESS.getValue(), null));
            } else {
                outstandingQoS1Acks.add(packet.getPacketIdentifier());
            }
        }
    }

    public String readIncoming() throws IOException {
        List<BrokerPacket> packets = adapter.readPackets();
        acknowledgeQos1Subscriptions(packets);
        processSubscriptionRequests(packets);
        packets = (maxSimilarPacketsInResponse > 0) ? filterSimilarPackets(packets) : packets;
//        Collections.sort(packets,
//                Comparator.comparing(MQTTControlPacket::getPacketType));

        return packetFormatters.apply(packets);
    }

    public boolean deleteRetained() throws IOException {
        if (retainedMessageTopics.isEmpty())
            return true;
        //System.out.println("Deleting " + retainedMessageTopics);
        //System.out.println(identifier + ": Deleting retained messages from topics " + String.join(", ", retainedMessageTopics));

        // send publish with 0 payload
        List<Boolean> ackChecks = new ArrayList<>();
        for (String topic : retainedMessageTopics) {
            if(topic.contains("\n") || topic.contains("\t") || topic.contains("\u0000") || topic.contains("\u001b"))
                continue;
            int packetIdentifier = packetIdSupplier.getAsInt();
            PublishPacket publishPacket = new PublishPacket(false, packetIdentifier, null, new MQTTMessage(topic, "", (byte) 1, true));
            List<BrokerPacket> responsePackets = adapter.communicate(publishPacket);
            ackChecks.add(!responsePackets.isEmpty() && responsePackets.size() == responsePackets.stream()
                    .filter(packet -> packet.getPacketType() == PacketType.PUBACK)
                    .map(packet -> (PubAckPacket) packet)
                    .filter(packet -> packet.getPacketIdentifier() == packetIdentifier
                            && EnumSet.of(ReasonCode.SUCCESS, ReasonCode.NO_MATCHING_SUBSCRIBERS).contains(packet.getReasonCode()))
                    .count());
        }
        retainedMessageTopics.clear();

        if (ackChecks.contains(false)) {
            System.out.println(identifier + ": Error deleting all retained messages");
        }

        return !ackChecks.contains(false);
    }

    private boolean unsubscribeFromAll() throws IOException {
        List<String> topics = new ArrayList<>(subscribedTopics.keySet());
        if (topics.isEmpty())
            return true;

//        System.out.println(identifier + ": Unsubscribing from " + String.join(", ", topics));

        int packetIdentifier = packetIdSupplier.getAsInt();
        UnsubscribePacket unsubscribePacket = new UnsubscribePacket(packetIdentifier, null, topics);
        List<BrokerPacket> responsePackets = adapter.communicate(unsubscribePacket);

        boolean result = !responsePackets.isEmpty() && responsePackets.size() == responsePackets.stream()
                .filter(packet -> packet.getPacketType() == PacketType.UNSUBACK)
                .map(packet -> (UnsubAckPacket) packet)
                .filter(packet -> packet.getPacketIdentifier() == packetIdentifier && packet.getReasonCodes().equals(Collections.nCopies(topics.size(), ReasonCode.SUCCESS)))
                .count();

        if (!result) {
            System.out.println(identifier + ": Error unsubscribing from all topics");
        }
        subscribedTopics.clear();

        return result;
    }

    private boolean cleanUpNecessary() {
        return !(subscriptionRequests.isEmpty() && subscribedTopics.isEmpty() && retainedMessageTopics.isEmpty());
    }

    private boolean checkedDisconnect() throws IOException {
        disconnectFromBroker();
        List<BrokerPacket> packets = adapter.readPackets();
        return packets.stream().allMatch(packet -> packet instanceof ConnectionClosed);
    }

    private boolean sendOutstandingAcks() {
        return adapterConfig.autoAckQoS1;
    }

    public boolean resetAndDisconnect() throws IOException {
        boolean success = true;

        if (cleanUpNecessary()) {
            if (adapter.isConnected()) {
                disconnectFromBroker();
                readIncoming();
                closeTCPSession();
            }
            startTCPSession();
            connectToBroker(true); // broker should discard session
            readIncoming();

            success = deleteRetained() && success;
            success = unsubscribeFromAll() && success;
            success = checkedDisconnect() && success;
            success = sendOutstandingAcks() && success;
            closeTCPSession();
        } else if (adapter.isConnected()) {
            success = checkedDisconnect();
            closeTCPSession();
        }

        hasBeenConnected = false;
        return success;
    }
}
