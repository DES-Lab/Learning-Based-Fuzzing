package fuzzing;

import mealyMachine.MealyState;
import sut.MQTTClientWrapper;

import java.io.IOException;
import java.util.*;

public class DemoFuzzingMapper implements FuzzingMapper{
    private List<String> usedInputs = new ArrayList<>();
    private final Random random = new Random();
    private RandomString randomString = new RandomString();

    @Override
    public FuzzOutputPair fuzzInputSymbol(MealyState state, String methodInput, MQTTClientWrapper client) throws IOException {
        String topic;
        switch (methodInput){
            case "connect":
                return new FuzzOutputPair("", client.connect());
            case "disconnect":
                clearMapperData();
                return new FuzzOutputPair("", client.disconnect());
            case "subscribe":
                topic = getNewFuzzedTopicOrExisting();
                usedInputs.add(topic);
                return new FuzzOutputPair(topic, client.subscribe(topic));
            case "unsubscribe":
                topic = usedInputs.isEmpty() ? getNewFuzzedTopicOrExisting() : usedInputs.get(0);
                usedInputs.remove(topic);
                return new FuzzOutputPair(topic, client.unsubscribe(topic));
            case "publish":
                if(state.getName().equals("s2"))
                    topic = usedInputs.get(random.nextInt(usedInputs.size()));
                else
                    topic = getNewFuzzedTopicOrExisting();
                return new FuzzOutputPair(topic, client.publish(topic, topic));
            case "invalid":
                switch (random.nextInt(4)) {
                    case 0:
                        topic = randomString.insertInvalidCharInTopic(getNewFuzzedTopicOrExisting());
                        return new FuzzOutputPair(topic, client.subscribe(topic));
                    case 1:
                        topic = randomString.insertInvalidCharInTopic(getNewFuzzedTopicOrExisting());
                        return new FuzzOutputPair(topic, client.unsubscribe(topic));
                    case 2:
                        topic = randomString.insertInvalidCharInTopic(getNewFuzzedTopicOrExisting());
                        return new FuzzOutputPair(topic, client.publish(topic, topic));
                    case 3:
                        topic = randomString.getSystemTopic(30);
                        topic = random.nextBoolean() ? randomString.insertInvalidCharInTopic(topic) : topic;
                        return new FuzzOutputPair(topic, client.publish(topic, topic));
                }
            default:
                throw new IllegalStateException("Unexpected value: " + methodInput);
        }
    }

    private String getNewFuzzedTopicOrExisting(){
        String topic;
        if(!usedInputs.isEmpty())
        return random.nextBoolean() ? randomString.getFuzzedTopic(30) : usedInputs.get(random.nextInt(usedInputs.size()));

        topic = randomString.getFuzzedTopic(30);
        return topic;

    }

    @Override
    public void clearMapperData() {
        usedInputs.clear();
    }
}
