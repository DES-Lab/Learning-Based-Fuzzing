package MQTTClient.data;

import MQTTClient.utils.MQTTUtils;
import com.sun.tools.javac.util.Pair;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

public class VariableHeaderProperties {
    // Allowed properties per type (Specification p25)
    private static final Map<PacketPropertyType, List<PacketType>> allowedPacketsPerProperty;
    static {
        allowedPacketsPerProperty = new HashMap<>();
        allowedPacketsPerProperty.put(PacketPropertyType.PAYLOAD_FORMAT_INDICATOR, Arrays.asList(PacketType.PUBLISH, PacketType.WILL));
        allowedPacketsPerProperty.put(PacketPropertyType.MESSAGE_EXPIRY_INTERVAL, Arrays.asList(PacketType.PUBLISH, PacketType.WILL));
        allowedPacketsPerProperty.put(PacketPropertyType.CONTENT_TYPE, Arrays.asList(PacketType.PUBLISH, PacketType.WILL));
        allowedPacketsPerProperty.put(PacketPropertyType.RESPONSE_TOPIC, Arrays.asList(PacketType.PUBLISH, PacketType.WILL));
        allowedPacketsPerProperty.put(PacketPropertyType.CORRELATION_DATA, Arrays.asList(PacketType.PUBLISH, PacketType.WILL));
        allowedPacketsPerProperty.put(PacketPropertyType.SUBSCRIPTION_IDENTIFIER, Arrays.asList(PacketType.PUBLISH, PacketType.SUBSCRIBE));
        allowedPacketsPerProperty.put(PacketPropertyType.SESSION_EXPIRY_INTERVAL, Arrays.asList(PacketType.CONNECT, PacketType.CONNACK, PacketType.DISCONNECT));
        allowedPacketsPerProperty.put(PacketPropertyType.ASSIGNED_CLIENT_IDENTIFIER, Arrays.asList(PacketType.CONNACK));
        allowedPacketsPerProperty.put(PacketPropertyType.SERVER_KEEP_ALIVE, Arrays.asList(PacketType.CONNACK));
        allowedPacketsPerProperty.put(PacketPropertyType.AUTHENTICATION_METHOD, Arrays.asList(PacketType.CONNECT, PacketType.CONNACK, PacketType.AUTH));
        allowedPacketsPerProperty.put(PacketPropertyType.AUTHENTICATION_DATA, Arrays.asList(PacketType.CONNECT, PacketType.CONNACK, PacketType.AUTH));
        allowedPacketsPerProperty.put(PacketPropertyType.REQUEST_PROBLEM_INFORMATION, Arrays.asList(PacketType.CONNECT));
        allowedPacketsPerProperty.put(PacketPropertyType.WILL_DELAY_INTERVAL, Arrays.asList(PacketType.WILL));
        allowedPacketsPerProperty.put(PacketPropertyType.REQUEST_RESPONSE_INFORMATION, Arrays.asList(PacketType.CONNECT));
        allowedPacketsPerProperty.put(PacketPropertyType.RESPONSE_INFORMATION, Arrays.asList(PacketType.CONNACK));
        allowedPacketsPerProperty.put(PacketPropertyType.SERVER_REFERENCE, Arrays.asList(PacketType.CONNACK, PacketType.DISCONNECT));
        allowedPacketsPerProperty.put(PacketPropertyType.REASON_STRING, Arrays.asList(PacketType.CONNACK, PacketType.PUBACK, PacketType.PUBREC,
                PacketType.PUBREL, PacketType.PUBCOMP, PacketType.SUBACK, PacketType.UNSUBACK, PacketType.DISCONNECT, PacketType.AUTH));
        allowedPacketsPerProperty.put(PacketPropertyType.RECEIVE_MAXIMUM, Arrays.asList(PacketType.CONNECT, PacketType.CONNACK));
        allowedPacketsPerProperty.put(PacketPropertyType.TOPIC_ALIAS_MAXIMUM, Arrays.asList(PacketType.CONNECT, PacketType.CONNACK));
        allowedPacketsPerProperty.put(PacketPropertyType.TOPIC_ALIAS, Arrays.asList(PacketType.PUBLISH));
        allowedPacketsPerProperty.put(PacketPropertyType.MAXIMUM_QOS, Arrays.asList(PacketType.CONNACK));
        allowedPacketsPerProperty.put(PacketPropertyType.RETAIN_AVAILABLE, Arrays.asList(PacketType.CONNACK));
        allowedPacketsPerProperty.put(PacketPropertyType.USER_PROPERTY, Arrays.asList(PacketType.CONNECT, PacketType.CONNACK, PacketType.PUBLISH,
                PacketType.PUBACK, PacketType.PUBREC, PacketType.PUBREL, PacketType.PUBCOMP, PacketType.SUBSCRIBE, PacketType.SUBACK,
                PacketType.UNSUBSCRIBE, PacketType.UNSUBACK, PacketType.DISCONNECT, PacketType.AUTH, PacketType.WILL));
        allowedPacketsPerProperty.put(PacketPropertyType.MAXIMUM_PACKET_SIZE, Arrays.asList(PacketType.CONNECT, PacketType.CONNACK));
        allowedPacketsPerProperty.put(PacketPropertyType.WILDCARD_SUBSCRIPTION_AVAILABLE, Arrays.asList(PacketType.CONNACK));
        allowedPacketsPerProperty.put(PacketPropertyType.SUBSCRIPTION_IDENTIFIER_AVAILABLE, Arrays.asList(PacketType.CONNACK));
        allowedPacketsPerProperty.put(PacketPropertyType.SHARED_SUBSCRIPTION_AVAILABLE, Arrays.asList(PacketType.CONNACK));
    }

    // Long = Variable Byte Integer
    // TODO: allow multiple "User Property" properties (or just ignore it since meaning is not specified?)
    private Map<PacketPropertyType, PacketProperty> properties = new LinkedHashMap<>();


    public PacketProperty<Byte> getPayloadFormatIndicator() {
        return properties.get(PacketPropertyType.PAYLOAD_FORMAT_INDICATOR);
    }

    public void setPayloadFormatIndicator(Byte payloadFormatIndicator) {
        properties.put(PacketPropertyType.PAYLOAD_FORMAT_INDICATOR, new PacketProperty<>(PacketPropertyType.PAYLOAD_FORMAT_INDICATOR, payloadFormatIndicator));
    }

    public PacketProperty<Integer> getMessageExpiryInterval() {
        return properties.get(PacketPropertyType.MESSAGE_EXPIRY_INTERVAL);
    }

    public void setMessageExpiryInterval(Integer messageExpiryInterval) {
        properties.put(PacketPropertyType.MESSAGE_EXPIRY_INTERVAL, new PacketProperty<>(PacketPropertyType.MESSAGE_EXPIRY_INTERVAL, messageExpiryInterval));
    }

    public PacketProperty<String> getContentType() {
        return properties.get(PacketPropertyType.CONTENT_TYPE);
    }

    public void setContentType(String contentType) {
        properties.put(PacketPropertyType.CONTENT_TYPE, new PacketProperty<>(PacketPropertyType.CONTENT_TYPE, contentType));
    }

    public PacketProperty<String> getResponseTopic() {
        return properties.get(PacketPropertyType.RESPONSE_TOPIC);
    }

    public void setResponseTopic(String responseTopic) {
        properties.put(PacketPropertyType.RESPONSE_TOPIC, new PacketProperty<>(PacketPropertyType.RESPONSE_TOPIC, responseTopic));
    }

    public PacketProperty<byte[]> getCorrelationData() {
        return properties.get(PacketPropertyType.CORRELATION_DATA);
    }

    public void setCorrelationData(byte[] correlationData) {
        properties.put(PacketPropertyType.CORRELATION_DATA, new PacketProperty<>(PacketPropertyType.CORRELATION_DATA, correlationData));
    }

    public PacketProperty<Long> getSubscriptionIdentifier() {
        return properties.get(PacketPropertyType.SUBSCRIPTION_IDENTIFIER);
    }

    public void setSubscriptionIdentifier(Long subscriptionIdentifier) {
        properties.put(PacketPropertyType.SUBSCRIPTION_IDENTIFIER, new PacketProperty<>(PacketPropertyType.SUBSCRIPTION_IDENTIFIER, subscriptionIdentifier));
    }

    public PacketProperty<Integer> getSessionExpiryInterval() {
        return properties.get(PacketPropertyType.SESSION_EXPIRY_INTERVAL);
    }

    public void setSessionExpiryInterval(Integer sessionExpiryInterval) {
        properties.put(PacketPropertyType.SESSION_EXPIRY_INTERVAL, new PacketProperty<>(PacketPropertyType.SESSION_EXPIRY_INTERVAL, sessionExpiryInterval));
    }

    public PacketProperty<String> getAssignedClientIdentifier() {
        return properties.get(PacketPropertyType.ASSIGNED_CLIENT_IDENTIFIER);
    }

    public void setAssignedClientIdentifier(String assignedClientIdentifier) {
        properties.put(PacketPropertyType.ASSIGNED_CLIENT_IDENTIFIER, new PacketProperty<>(PacketPropertyType.ASSIGNED_CLIENT_IDENTIFIER, assignedClientIdentifier));
    }

    public PacketProperty<Short> getServerKeepAlive() {
        return properties.get(PacketPropertyType.SERVER_KEEP_ALIVE);
    }

    public void setServerKeepAlive(Short serverKeepAlive) {
        properties.put(PacketPropertyType.SERVER_KEEP_ALIVE, new PacketProperty<>(PacketPropertyType.SERVER_KEEP_ALIVE, serverKeepAlive));
    }

    public PacketProperty<String> getAuthenticationMethod() {
        return properties.get(PacketPropertyType.AUTHENTICATION_METHOD);
    }

    public void setAuthenticationMethod(String authenticationMethod) {
        properties.put(PacketPropertyType.AUTHENTICATION_METHOD, new PacketProperty<>(PacketPropertyType.AUTHENTICATION_METHOD, authenticationMethod));
    }

    public PacketProperty<byte[]> getAuthenticationData() {
        return properties.get(PacketPropertyType.AUTHENTICATION_DATA);
    }

    public void setAuthenticationData(byte[] authenticationData) {
        properties.put(PacketPropertyType.AUTHENTICATION_DATA, new PacketProperty<>(PacketPropertyType.AUTHENTICATION_DATA, authenticationData));
    }

    public PacketProperty<Byte> getRequestProblemInformation() {
        return properties.get(PacketPropertyType.REQUEST_PROBLEM_INFORMATION);
    }

    public void setRequestProblemInformation(Byte requestProblemInformation) {
        properties.put(PacketPropertyType.REQUEST_PROBLEM_INFORMATION, new PacketProperty<>(PacketPropertyType.REQUEST_PROBLEM_INFORMATION, requestProblemInformation));
    }

    public PacketProperty<Integer> getWillDelayInterval() {
        return properties.get(PacketPropertyType.WILL_DELAY_INTERVAL);
    }

    public void setWillDelayInterval(Integer willDelayInterval) {
        properties.put(PacketPropertyType.WILL_DELAY_INTERVAL, new PacketProperty<>(PacketPropertyType.WILL_DELAY_INTERVAL, willDelayInterval));
    }

    public PacketProperty<Byte> getRequestResponseInformation() {
        return properties.get(PacketPropertyType.REQUEST_RESPONSE_INFORMATION);
    }

    public void setRequestResponseInformation(Byte requestResponseInformation) {
        properties.put(PacketPropertyType.REQUEST_RESPONSE_INFORMATION, new PacketProperty<>(PacketPropertyType.REQUEST_RESPONSE_INFORMATION, requestResponseInformation));
    }

    public PacketProperty<String> getResponseInformation() {
        return properties.get(PacketPropertyType.RESPONSE_INFORMATION);
    }

    public void setResponseInformation(String responseInformation) {
        properties.put(PacketPropertyType.RESPONSE_INFORMATION, new PacketProperty<>(PacketPropertyType.RESPONSE_INFORMATION, responseInformation));
    }

    public PacketProperty<String> getServerReference() {
        return properties.get(PacketPropertyType.SERVER_REFERENCE);
    }

    public void setServerReference(String serverReference) {
        properties.put(PacketPropertyType.SERVER_REFERENCE, new PacketProperty<>(PacketPropertyType.SERVER_REFERENCE, serverReference));
    }

    public PacketProperty<String> getReasonString() {
        return properties.get(PacketPropertyType.REASON_STRING);
    }

    public void setReasonString(String reasonString) {
        properties.put(PacketPropertyType.REASON_STRING, new PacketProperty<>(PacketPropertyType.REASON_STRING, reasonString));
    }

    public PacketProperty<Short> getReceiveMaximum() {
        return properties.get(PacketPropertyType.RECEIVE_MAXIMUM);
    }

    public void setReceiveMaximum(Short receiveMaximum) {
        properties.put(PacketPropertyType.RECEIVE_MAXIMUM, new PacketProperty<>(PacketPropertyType.RECEIVE_MAXIMUM, receiveMaximum));
    }

    public PacketProperty<Short> getTopicAliasMaximum() {
        return properties.get(PacketPropertyType.TOPIC_ALIAS_MAXIMUM);
    }

    public void setTopicAliasMaximum(Short topicAliasMaximum) {
        properties.put(PacketPropertyType.TOPIC_ALIAS_MAXIMUM, new PacketProperty<>(PacketPropertyType.TOPIC_ALIAS_MAXIMUM, topicAliasMaximum));
    }

    public PacketProperty<Short> getTopicAlias() {
        return properties.get(PacketPropertyType.TOPIC_ALIAS);
    }

    public void setTopicAlias(Short topicAlias) {
        properties.put(PacketPropertyType.TOPIC_ALIAS, new PacketProperty<>(PacketPropertyType.TOPIC_ALIAS, topicAlias));
    }

    public PacketProperty<Byte> getMaximumQoS() {
        return properties.get(PacketPropertyType.MAXIMUM_QOS);
    }

    public void setMaximumQoS(Byte maximumQoS) {
        properties.put(PacketPropertyType.MAXIMUM_QOS, new PacketProperty<>(PacketPropertyType.MAXIMUM_QOS, maximumQoS));
    }

    public PacketProperty<Byte> getRetainAvailable() {
        return properties.get(PacketPropertyType.RETAIN_AVAILABLE);
    }

    public void setRetainAvailable(Byte retainAvailable) {
        properties.put(PacketPropertyType.RETAIN_AVAILABLE, new PacketProperty<>(PacketPropertyType.RETAIN_AVAILABLE, retainAvailable));
    }

    public PacketProperty<Pair<String, String>> getUserProperty() {
        return properties.get(PacketPropertyType.USER_PROPERTY);
    }

    public void setUserProperty(Pair<String, String> userProperty) {
        properties.put(PacketPropertyType.USER_PROPERTY, new PacketProperty<>(PacketPropertyType.USER_PROPERTY, userProperty));
    }

    public PacketProperty<Integer> getMaximumPacketSize() {
        return properties.get(PacketPropertyType.MAXIMUM_PACKET_SIZE);
    }

    public void setMaximumPacketSize(Integer maximumPacketSize) {
        properties.put(PacketPropertyType.MAXIMUM_PACKET_SIZE, new PacketProperty<>(PacketPropertyType.MAXIMUM_PACKET_SIZE, maximumPacketSize));
    }

    public PacketProperty<Byte> getWildcardSubscriptionAvailable() {
        return properties.get(PacketPropertyType.WILDCARD_SUBSCRIPTION_AVAILABLE);
    }

    public void setWildcardSubscriptionAvailable(Byte wildcardSubscriptionAvailable) {
        properties.put(PacketPropertyType.WILDCARD_SUBSCRIPTION_AVAILABLE, new PacketProperty<>(PacketPropertyType.WILDCARD_SUBSCRIPTION_AVAILABLE, wildcardSubscriptionAvailable));
    }

    public PacketProperty<Byte> getSubscriptionIdentifierAvailable() {
        return properties.get(PacketPropertyType.SUBSCRIPTION_IDENTIFIER_AVAILABLE);
    }

    public void setSubscriptionIdentifierAvailable(Byte subscriptionIdentifierAvailable) {
        properties.put(PacketPropertyType.SUBSCRIPTION_IDENTIFIER_AVAILABLE, new PacketProperty<>(PacketPropertyType.SUBSCRIPTION_IDENTIFIER_AVAILABLE, subscriptionIdentifierAvailable));
    }

    public PacketProperty<Byte> getSharedSubscriptionAvailable() {
        return properties.get(PacketPropertyType.SHARED_SUBSCRIPTION_AVAILABLE);
    }

    public void setSharedSubscriptionAvailable(Byte sharedSubscriptionAvailable) {
        properties.put(PacketPropertyType.SHARED_SUBSCRIPTION_AVAILABLE, new PacketProperty<>(PacketPropertyType.SHARED_SUBSCRIPTION_AVAILABLE, sharedSubscriptionAvailable));
    }


    public boolean validForPacketType(PacketType packetType) {
        for (PacketPropertyType property : properties.keySet()) {
            List<PacketType> allowedPackets = allowedPacketsPerProperty.getOrDefault(property, Collections.emptyList());

            if (!allowedPackets.contains(packetType)) {
                return false;
            }
        }

        return true;
    }

    public boolean isEmpty() {
        return properties.isEmpty();
    }

    public List<Byte> toBinary() {
        List<Byte> data = new ArrayList<>();

        for (PacketProperty property : properties.values()) {
            // identifier
            data.addAll(MQTTUtils.encodeVariableByteInteger(property.getType().getId()));

            // value
            Object value = property.getValue();
            if (value instanceof Byte) {
                data.add((Byte) value);
            } else if (value instanceof Integer) {
                Integer val = (Integer) value;
                data.add((byte)((val >>> 24) & 0xFF));
                data.add((byte)((val >>> 16) & 0xFF));
                data.add((byte)((val >>> 8) & 0xFF));
                data.add((byte)(val & 0xFF));
            } else if (value instanceof String) {
                data.addAll(MQTTUtils.encodeString((String) value));
            } else if (value instanceof byte[]) { // binary data
                data.addAll(MQTTUtils.encodeBinaryData((byte[]) value));
            } else if (value instanceof Long) { // Long = Variable Byte Integer
                data.addAll(MQTTUtils.encodeVariableByteInteger(((Long) value).intValue()));
            } else if (value instanceof Short) {
                Short val = (Short) value;
                data.add((byte)((val >>> 8) & 0xFF));
                data.add((byte)(val & 0xFF));
            } else if (value instanceof Pair) {
                Pair<String, String> val = (Pair<String, String>) value;
                data.addAll(MQTTUtils.encodeString(val.fst));
                data.addAll(MQTTUtils.encodeString(val.snd));
            } else {
                throw new IllegalArgumentException("Value of property " + property.getType().name() + " is of invalid type " + value.getClass().getName());
            }
        }

        // length of properties at the beginning
        data.addAll(0, MQTTUtils.encodeVariableByteInteger(data.size()));

        return data;
    }

    public static VariableHeaderProperties parseVariableHeaderProperties(ByteBuffer data, int propertiesLength) {
        VariableHeaderProperties vhProperties = new VariableHeaderProperties();

        try {
            int endOfProperties = data.position() + propertiesLength;
            while (data.position() < endOfProperties) {
                // identifier
                int id = MQTTUtils.decodeVariableByteInteger(data);

                PacketPropertyType propertyType = PacketPropertyType.valueOf(id);
                switch (propertyType) {
                    // Byte
                    case PAYLOAD_FORMAT_INDICATOR:
                    case REQUEST_PROBLEM_INFORMATION:
                    case REQUEST_RESPONSE_INFORMATION:
                    case MAXIMUM_QOS:
                    case RETAIN_AVAILABLE:
                    case WILDCARD_SUBSCRIPTION_AVAILABLE:
                    case SUBSCRIPTION_IDENTIFIER_AVAILABLE:
                    case SHARED_SUBSCRIPTION_AVAILABLE: {
                        Byte value = data.get();
                        vhProperties.properties.put(propertyType, new PacketProperty<>(propertyType, value));
                        break;
                    }
                    // 2 Byte Integer
                    case SERVER_KEEP_ALIVE:
                    case RECEIVE_MAXIMUM:
                    case TOPIC_ALIAS_MAXIMUM:
                    case TOPIC_ALIAS: {
                        short value = data.getShort();
                        vhProperties.properties.put(propertyType, new PacketProperty<>(propertyType, value));
                        break;
                    }
                    // 4 Byte Integer
                    case MESSAGE_EXPIRY_INTERVAL:
                    case SESSION_EXPIRY_INTERVAL:
                    case WILL_DELAY_INTERVAL:
                    case MAXIMUM_PACKET_SIZE: {
                        int value = data.getInt();
                        vhProperties.properties.put(propertyType, new PacketProperty<>(propertyType, value));
                        break;
                    }
                    // Variable Byte Integer
                    case SUBSCRIPTION_IDENTIFIER: {
                        Long value = (long) MQTTUtils.decodeVariableByteInteger(data);
                        vhProperties.properties.put(propertyType, new PacketProperty<>(propertyType, value));

                        break;
                    }
                    // String
                    case CONTENT_TYPE:
                    case RESPONSE_TOPIC:
                    case ASSIGNED_CLIENT_IDENTIFIER:
                    case AUTHENTICATION_METHOD:
                    case RESPONSE_INFORMATION:
                    case SERVER_REFERENCE:
                    case REASON_STRING: {
                        String value = MQTTUtils.decodeString(data, true);
                        vhProperties.properties.put(propertyType, new PacketProperty<>(propertyType, value));

                        break;
                    }
                    // String Pair
                    case USER_PROPERTY: {
                        String key = MQTTUtils.decodeString(data, true);
                        String value = MQTTUtils.decodeString(data, true);

                        vhProperties.properties.put(propertyType, new PacketProperty<>(propertyType, new Pair<>(key, value)));

                        break;
                    }
                    // Binary Data
                    case CORRELATION_DATA:
                    case AUTHENTICATION_DATA: {
                        int lengthOfData = ((int) data.getShort()) & 0xFFFF;
                        byte[] binaryData = new byte[lengthOfData];
                        data.get(binaryData);

                        vhProperties.properties.put(propertyType, new PacketProperty<>(propertyType, binaryData));

                        break;
                    }

                    default:
                        throw new NotImplementedException();
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Malformed Packet. Could not parse Variable Header Properties: " + e.getMessage());
        }

        return vhProperties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VariableHeaderProperties that = (VariableHeaderProperties) o;
        return Objects.equals(properties, that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(properties);
    }

    @Override
    public String toString() {
        return properties.values().stream().map(PacketProperty::toString).collect(Collectors.joining(", "));
    }
}