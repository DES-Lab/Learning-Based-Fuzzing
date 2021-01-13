# Learning-Based Fuzzing of IoT Message Brokers


## Different Brokers Configurations

* VerneMQ
    * docker run -p 1886:1883  -e "DOCKER_VERNEMQ_ALLOW_ANONYMOUS=on" -e "DOCKER_VERNEMQ_ACCEPT_EULA=yes" -e "DOCKER_VERNEMQ_listener.tcp.allowed_protocol_versions=5" --name vernemq1 -d vernemq/vernemq
* Mosquitto
    * Port 1885
    * /usr/local/sbin/mosquitto -p 1885
* HiveMq
    * Port 1884 selected in /conf/config.xml 
    * ./Documents/hivemq-ce-2020.2/bin/run.sh
* EMQ
    * Port 1883
    * emqx start
* ejabbard
    * disable authentication
    * port 1997
    
## To reporduce

- Once all brokers are installed and appropriate ports assigned to them, run

```
    Linux/MAC
    ./gradlew build
    ./gradlew learningBasedFuzzing
    Windows
    ./gradlew.bat build
    ./gradlew.bat learningBasedFuzzing
```    
    
## Short code explanation

Learning of the Mosquitto MQTT broker and it's use as a basis for further model-based fuzzing is found in the Main.java class.
 
 - Create the MQTT client which will interact with the Mosquitto broker
 
``
MQTTBrokerAdapterConfig mosquittoBrokerConfig =
                new MQTTBrokerAdapterConfig(InetAddress.getByName("127.0.0.1"), 1885, 200, true, true, true);
        MQTTClientWrapper mosquittoClient = new MQTTClientWrapper("c0", "mosquitto", mosquittoBrokerConfig);
``

 - Create input alphabeth for learning and corresponding mapper that translates abstract inputs to concrete ones
 - Start the experiment
 - Results are saved in the 'learnedModels' folder
 - Comment out the last line if you want to skip learning and use model found in 'learnedModels' folder
```
Learner mosquitoLearner = new Learner(new LearningMapper(mosquittoClient));
 List<String> paperExample = Arrays.asList(
                "connect", "disconnect",
                "subscribe","publish", "unsubscribe",
                "invalid");

String experimentName = "MosquittoDemonstation";
mosquitoLearner.learn(3000, experimentName, paperExample); // COMMENT OUT if you want to skip learning and usead already existing model
```
- Define configuration and clients for other brokers
- Same input alphabet is used for the fuzzing mapper
- Define number of random walks and its maximum lenght
```
FuzzingBasedTesting fuzzingBasedTesting =
                new FuzzingBasedTesting("learnedModels/MosquittoDemonstation.dot", clients, new DemoFuzzingMapper());

fuzzingBasedTesting.setInputAlphabet(paperExample);

fuzzingBasedTesting.randomWalkWithFuzzing(1000, 10);
```
# Learning-Based-Fuzzing
