package MQTTClient.data;
import java.util.Arrays;

public enum PacketPropertyType {
    PAYLOAD_FORMAT_INDICATOR(1),
    MESSAGE_EXPIRY_INTERVAL(2),
    CONTENT_TYPE(3),
    RESPONSE_TOPIC(8),
    CORRELATION_DATA(9),
    SUBSCRIPTION_IDENTIFIER(11),
    SESSION_EXPIRY_INTERVAL(17),
    ASSIGNED_CLIENT_IDENTIFIER(18),
    SERVER_KEEP_ALIVE(19),
    AUTHENTICATION_METHOD(21),
    AUTHENTICATION_DATA(22),
    REQUEST_PROBLEM_INFORMATION(23),
    WILL_DELAY_INTERVAL(24),
    REQUEST_RESPONSE_INFORMATION(25),
    RESPONSE_INFORMATION(26),
    SERVER_REFERENCE(28),
    REASON_STRING(31),
    RECEIVE_MAXIMUM(33),
    TOPIC_ALIAS_MAXIMUM(34),
    TOPIC_ALIAS(35),
    MAXIMUM_QOS(36),
    RETAIN_AVAILABLE(37),
    USER_PROPERTY(38),
    MAXIMUM_PACKET_SIZE(39),
    WILDCARD_SUBSCRIPTION_AVAILABLE(40),
    SUBSCRIPTION_IDENTIFIER_AVAILABLE(41),
    SHARED_SUBSCRIPTION_AVAILABLE(42);

    private int id;

    PacketPropertyType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static PacketPropertyType valueOf(int id) {
        return Arrays.stream(values())
                .filter(ppt -> ppt.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Identifier %d does not match a PropertyType", id)));
    }
}
