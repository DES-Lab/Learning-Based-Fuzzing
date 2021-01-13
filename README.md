# Supplemental Materials for "Learning-Based Fuzzing of IoT Message Brokers"


## Getting Started

### MQTT Broker Configurations

* VerneMQ 1.11.0
    * https://github.com/vernemq/vernemq
    * ```docker run -p 1886:1883  -e "DOCKER_VERNEMQ_ALLOW_ANONYMOUS=on" -e "DOCKER_VERNEMQ_ACCEPT_EULA=yes" -e "DOCKER_VERNEMQ_listener.tcp.allowed_protocol_versions=5" --name vernemq1 -d vernemq/vernemq```
* Eclipse Mosquitto 1.6.8
    * https://mosquitto.org/download/
    * Port 1885
    * ```mosquitto -p 1885```
* HiveMq 2020.2
    * https://github.com/hivemq/hivemq-community-edition
    * Port 1884 (configuration in /hivemq-ce-2020.2/conf/config.xml)
    * ```./hivemq-ce-2020.2/bin/run.sh```
* EMQ X v4.0.0
    * https://github.com/emqx/emqx
    * ``` docker run -d --name emqx -p 1883:1883 -p 8083:8083 -p 8883:8883 -p 8084:8084 -p 18083:18083 emqx/emqx```
* ejabberd 20.7.0
    * https://github.com/processone/ejabberd
    * Port 1887
    * disable authentication
    
### Running the Tool

- After the MQTT broker setup the tool can be executed.

```
    Linux/MAC
    ./gradlew build
    ./gradlew learningBasedFuzzing
    Windows
    ./gradlew.bat build
    ./gradlew.bat learningBasedFuzzing
```    
    
## Brief Comments on the Code

In the Main.java following steps are performed.
 
 - Creation of the Eclipse Mosquitto adapter and MQTT client which will interact with the Eclipse Mosquitto broker
 
``
MQTTBrokerAdapterConfig mosquittoBrokerConfig =
                new MQTTBrokerAdapterConfig(InetAddress.getByName("127.0.0.1"), 1885, 200, true, true, true);
        MQTTClientWrapper mosquittoClient = new MQTTClientWrapper("c0", "mosquitto", mosquittoBrokerConfig);
``

 - Definition of the input alphabet for learning and the corresponding mapper that translates abstract inputs to concrete inputs
 - Learning of the Eclipse Mosquitto model
 - Results are saved in the 'learnedModels' folder
 - If you want to skip learning and use model in 'learnedModels' folder, remove (comment out) the last line:
```
Learner mosquittoLearner = new Learner(new LearningMapper(mosquittoClient));
 List<String> paperExample = Arrays.asList(
                "connect", "disconnect",
                "subscribe","publish", "unsubscribe",
                "invalid");

String experimentName = "MosquittoModel";
mosquittoLearner.learn(3000, experimentName, paperExample); // COMMENT OUT if you want to skip learning and use already existing model
```
- Configuration of clients for other brokers
- Same abstract input alphabet is used for the fuzzing mapper
- Define number of random walks and its maximum length
```
FuzzingBasedTesting fuzzingBasedTesting =
                new FuzzingBasedTesting("learnedModels/MosquittoModel.dot", clients, new DemoFuzzingMapper());

fuzzingBasedTesting.setInputAlphabet(paperExample);

fuzzingBasedTesting.randomWalkWithFuzzing(1000, 10);
```
