package MQTTClient.data;
import java.util.Objects;

public class PacketProperty<T> {
    private PacketPropertyType type;
    private T value;

    public PacketProperty(PacketPropertyType id, T value) {
        this.type = id;
        this.value = value;
    }

    public PacketPropertyType getType() {
        return type;
    }

    public T getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PacketProperty<?> that = (PacketProperty<?>) o;
        return type == that.type &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }

    @Override
    public String toString() {
        return String.format("%s = '%s'", type.toString(), value.toString());
    }
}
