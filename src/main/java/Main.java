import MQTTClient.data.MQTTBrokerAdapterConfig;
import fuzzing.DemoFuzzingMapper;
import fuzzing.FuzzingBasedTesting;
import learning.Learner;
import learning.LearningMapper;
import sut.MQTTClientWrapper;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws NoSuchMethodException, IOException, InterruptedException {
        // Mosquitto broker is assigned to the port 1885
        MQTTBrokerAdapterConfig mosquittoBrokerConfig =
                new MQTTBrokerAdapterConfig(InetAddress.getByName("127.0.0.1"), 1885, 200, true, true, true);
        MQTTClientWrapper mosquittoClient = new MQTTClientWrapper("c0", "mosquitto", mosquittoBrokerConfig);
        // define the mapper used for learning (abstract -> concrete inputs)
        Learner mosquitoLearner = new Learner(new LearningMapper(mosquittoClient));

        // learning input alphabet
        // 
        // invalid covers multiple invalid actions, such as subscribe, unsubscribe and publish with invalid input
        // and publish with system level topic
        List<String> paperExample = Arrays.asList(
                "connect", "disconnect",
                "subscribe","publish", "unsubscribe",
                "invalid");

        String experimentName = "MosquittoDemonstation";
        // learned model is saved to learnedModels/MosquittoDemonstation.dot
        mosquitoLearner.learn(3000, experimentName, paperExample); // COMMENT OUT if you want to skip learning and usead already existing model

        // Define other brokers that are going to be fuzzed
        MQTTClientWrapper hiveMQClient = new MQTTClientWrapper("c0", "hiveMQ",
                new MQTTBrokerAdapterConfig(InetAddress.getByName("127.0.0.1"), 1884, 200, true, true, true));
        MQTTClientWrapper verneMQClient = new MQTTClientWrapper("c0", "verneMQ",
                new MQTTBrokerAdapterConfig(InetAddress.getByName("127.0.0.1"), 1886, 200, true, true, true));
        MQTTClientWrapper EMQXClient = new MQTTClientWrapper("c0", "EMQX",
                new MQTTBrokerAdapterConfig(InetAddress.getByName("127.0.0.1"), 1883, 200, true, true, true));
        MQTTClientWrapper ejabbardClient = new MQTTClientWrapper("c0", "ejabbard",
                new MQTTBrokerAdapterConfig(InetAddress.getByName("127.0.0.1"), 1887, 200, true, true, true));

        List<MQTTClientWrapper> clients =
                Arrays.asList(mosquittoClient, hiveMQClient, verneMQClient, EMQXClient, ejabbardClient);

        // define model that serves as a basis for fuzzing, broker setup data for each broker, and used fuzzing mapper
        FuzzingBasedTesting fuzzingBasedTesting =
                new FuzzingBasedTesting("learnedModels/MosquittoDemonstation.dot", clients, new DemoFuzzingMapper());

        // same input alphabet used for learning
        fuzzingBasedTesting.setInputAlphabet(paperExample);

        // define number of random walks and maximum lenght of each walk
        fuzzingBasedTesting.randomWalkWithFuzzing(1000, 10);
    }
}
