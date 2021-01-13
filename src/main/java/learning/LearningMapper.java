package learning;

import MQTTClient.data.MQTTBrokerAdapterConfig;
import de.learnlib.drivers.reflect.ConcreteMethodInput;
import de.learnlib.drivers.reflect.MethodInput;
import de.learnlib.drivers.reflect.ReturnValue;
import de.learnlib.mapper.api.SULMapper;
import sut.MQTTClientWrapper;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class LearningMapper implements SULMapper<String, String, ConcreteMethodInput, Object> {
    private MQTTClientWrapper client;
    private Method mSubscribe;
    private Method mConnect;
    private Method mDisconnect;
    private Method mUnsubscribe;
    private Method mPublish;
    private Random random;

    public LearningMapper(int port) throws NoSuchMethodException, UnknownHostException {
        MQTTBrokerAdapterConfig brokerConfig =
                new MQTTBrokerAdapterConfig(InetAddress.getByName("127.0.0.1"), port, 200, true, true, true);

        this.client = new MQTTClientWrapper("c0", "Client", brokerConfig);
        getMethods();
    }

    public LearningMapper(MQTTClientWrapper client) throws NoSuchMethodException, UnknownHostException {
        this.client = client;
        getMethods();
    }

    @Override
    public void pre() {
        client.pre();
    }

    @Override
    public void post() {
        try {
            client.post();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ConcreteMethodInput mapInput(String abstractInput) {
        switch (abstractInput){
            case "connect":
               return getConcreteMethod("connect", mConnect, Collections.emptyList());
            case "disconnect":
                return getConcreteMethod("disconnect", mDisconnect, Collections.emptyList());
            case "subscribe":
                return getConcreteMethod("subscribe", mSubscribe, Collections.singletonList("test/test/test"));
            case "publish":
                return getConcreteMethod("publish", mPublish, Arrays.asList("test/test/test", "Hello."));
            case "unsubscribe":
                return getConcreteMethod("unsubscribe", mUnsubscribe, Collections.singletonList("test/test/test"));
            // invalid, instead of invalid(publish), invalid(susbscribe)... makes the learning alphabet more compact
            // all invalid actions should behave the same (in the case of Mosqitto they do)
            case "invalid":
                switch (random.nextInt(3)) {
                    case 0:
                        return getConcreteMethod("invalidSubscribe", mSubscribe, Collections.singletonList("te\u0000st/test/test"));
                    case 1:
                        return getConcreteMethod("invalidUnsubscribe", mUnsubscribe, Collections.singletonList("te\u0000st/test/test"));
                    case 2:
                        return getConcreteMethod("invalidPublish", mPublish, Arrays.asList("i\u0000nternal/test/test", "Hello."));
                }
            default:
                throw new IllegalStateException("Unexpected value: " + abstractInput);
        }
    }

    private ConcreteMethodInput getConcreteMethod(String name, Method method, List<String> params){
        MethodInput mi = new MethodInput(name, method, new HashMap<>(), params.toArray());
        return new ConcreteMethodInput(mi, new HashMap<>(), client);
    }

    @Override
    public String mapOutput(Object concreteOutput) {
        return new ReturnValue(concreteOutput).toString();
    }

    private void getMethods() throws NoSuchMethodException {
        mConnect = MQTTClientWrapper.class.getMethod("connect");
        mDisconnect = MQTTClientWrapper.class.getMethod("disconnect");
        mSubscribe = MQTTClientWrapper.class.getMethod("subscribe", String.class);
        mUnsubscribe = MQTTClientWrapper.class.getMethod("unsubscribe", String.class);
        mPublish = MQTTClientWrapper.class.getMethod("publish", String.class, String.class);
        random = new Random();
    }

}
