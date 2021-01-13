package fuzzing;

import mealyMachine.MealyMachine;
import mealyMachine.MealyState;
import mealyMachine.OutputTransition;
import sut.MQTTClientWrapper;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FuzzingBasedTesting {
    MealyMachine mealyAutoamta;
    MealyState initialState;
    Random rand = new Random();
    List<String> inputAlphabet;
    FuzzingMapper fuzzingMapper;
    List<MQTTClientWrapper> clients;


    public FuzzingBasedTesting(String dotFileName, List<MQTTClientWrapper> clients, FuzzingMapper fuzzingMapper) throws UnknownHostException {
        mealyAutoamta = new MealyMachine(dotFileName);
        initialState = mealyAutoamta.getInitialState();
        this.clients = clients;
//        for (MQTTBrokerAdapterConfig conf: configs)
//            clients.add(new MQTTClientWrapper("c0", "Client", conf));
        this.fuzzingMapper = fuzzingMapper;
    }

    public void randomWalkWithFuzzing(int numWalks, int walkLenght) throws IOException {
        for (int i = 0; i < numWalks; i++) {
            MealyState currState = initialState;
            List<String> inputs = new ArrayList<>();
            List<String> outputsModel = new ArrayList<>();

            fuzzingMapper.clearMapperData();
            for (MQTTClientWrapper sut: clients) {
                sut.post();
            }

            for (int j = 0; j < walkLenght; j++) {
                String input = j == 0 ? "connect" : randomStep(currState);
                inputs.add(input);
                OutputTransition outputTransition = currState.getOutputTransition(input);
                currState = outputTransition.state;
                outputsModel.add(outputTransition.output);
            }
            for (MQTTClientWrapper sut: clients) {
                List<String> outputsSUT = new ArrayList<>();
                boolean stopFlag = false;
                currState = initialState;
                for (int j = 0; j < inputs.size(); j++) {
                    OutputTransition outputTransition = currState.getOutputTransition(inputs.get(j));
                    currState = outputTransition.state;

                    FuzzOutputPair output = fuzzingMapper.fuzzInputSymbol(currState, inputs.get(j), sut);
                    outputsSUT.add(output.getOutput());
                    if (!output.getOutput().equals(outputsModel.get(j))) {
                        stopFlag = true;
                        System.out.println("---------------------------------------");
                        System.out.println("Broker: " + sut.getName());
                        System.out.print(inputs.subList(0, outputsSUT.size()) + "   - inputs\n");
                        System.out.print(outputsModel.subList(0, outputsSUT.size()) + "   - model outputs\n");
                        System.out.print(outputsSUT + "   - SUT outputs\n");
                        System.out.print(output.getFuzzedString() + "   - fuzzed topic string\n");
                        System.out.println("Model : " + outputsModel.get(j) + " vs Actual " + output.getOutput());
                    }
                    if (stopFlag)
                        break;
                }
                fuzzingMapper.clearMapperData();
            }
        }
    }

    private String randomStep(MealyState state){
        return state.getInputs().get(rand.nextInt(state.getInputs().size()));
    }

    public void setInputAlphabet(List<String> inputAlphabet) {
        this.inputAlphabet = inputAlphabet;
    }
}
