package mealyMachine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class MealyMachine {
    private String fileName;
    private Set<MealyState> states;
    private MealyState initialState;

    public MealyMachine(String dotFilename){
        this.fileName = dotFilename;
        states = new HashSet<>();
        getStatesAndInitialState();
        getTransitions();

    }

    private void getStatesAndInitialState(){
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replaceAll("\\s+","");
                if(line.contains("s") && line.contains("shape=\"circle\"label=")){
                    states.add(new MealyState(line.split("\\[")[0]));
                }

                if(line.contains("__start") && line.contains("->")){
                    String stateName = line.split("->")[1];
                    stateName = stateName.substring(0, stateName.length() - 1);
                    for (MealyState state: states) {
                        if(stateName.equals(state.getName())){
                            this.initialState = state;
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void getTransitions(){
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replaceAll("\\s+","");
                if(line.contains("->") && !line.contains("__start")){
                    String[] states = line.split("->");
                    MealyState stateFrom = getState(states[0]);
                    MealyState stateTo = getState(states[1].split("\\[")[0]);
                    String transitionString = line.substring(line.indexOf("=\"")+2,line.indexOf("\"];"));
                    String[] transitions = transitionString.split("/");
                    assert stateFrom != null;
                    stateFrom.addTransition(transitions[0], transitions[1], stateTo);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MealyState getState(String name){
        for (MealyState state: this.states) {
            if(state.getName().equals(name))
                return state;
        }
        return null;
    }

    public List<String> traverseInput(List<String> inputs){
        MealyState currentState = initialState;
        List<String> outputs = new ArrayList<>();
        for (String input: inputs) {
            assert currentState != null;
            OutputTransition outputTransition = currentState.getOutputTransition(input);
            currentState = getState(outputTransition.state.getName());
            outputs.add(outputTransition.output);
        }
        return outputs;
    }

    public MealyState getNextState(MealyState currState, String input){
        OutputTransition outputTransition = currState.getOutputTransition(input);
        return getState(outputTransition.state.getName());
    }

    public String getFileName() {
        return fileName;
    }

    public MealyState getInitialState(){
        return initialState;
    }
}
