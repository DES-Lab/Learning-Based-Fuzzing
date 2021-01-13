package MQTTClient.packets;


import MQTTClient.data.PacketType;
import MQTTClient.data.ReasonCode;
import MQTTClient.data.VariableHeaderProperties;
import MQTTClient.utils.MQTTUtils;

import java.util.ArrayList;
import java.util.List;

public class DisconnectPacket implements ClientPacket, BrokerPacket {
    private ReasonCode reasonCode;
    private VariableHeaderProperties properties;

    public DisconnectPacket() {
        this((byte) 0x00, null);
    }

    public DisconnectPacket(byte reasonCode, VariableHeaderProperties properties) {
        checkProperties(properties);

        this.reasonCode = ReasonCode.valueOf(reasonCode, getPacketType());
        this.properties = (properties == null) ? new VariableHeaderProperties() : properties;
    }

    public ReasonCode getReasonCode() {
        return reasonCode;
    }

    public VariableHeaderProperties getProperties() {
        return properties;
    }

    @Override
    public List<Byte> toBinary() {
        List<Byte> data = new ArrayList<>();

        // Variable Header (can be omitted if no properties and normal disconnect)
        if (reasonCode != ReasonCode.NORMAL_DISCONNECTION || !properties.isEmpty()) {
            data.add(reasonCode.getValue());

            if (!properties.isEmpty()) // can be omitted
                data.addAll(properties.toBinary());
        }

        // Fixed Header at beginning
        data.addAll(0, MQTTUtils.encodeFixedHeader(getPacketType(), (byte) 0, data.size()));

        return data;
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.DISCONNECT;
    }

    @Override
    public String toString() {
        return getPacketType().name() + "{" +
                "reasonCode=" + reasonCode +
                ", properties=" + properties +
                '}';
    }


}
