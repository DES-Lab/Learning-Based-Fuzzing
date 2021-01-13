package mealyMachine;

import java.util.*;

public class MealyState {
    private String stateName;
    private Map<String, OutputTransition> transitions;

    public MealyState(String name){
        this.stateName = name;
        transitions = new HashMap<>();
    }

    public void addTransition(String input, String output, MealyState newState){
        transitions.put(input, new OutputTransition(output, newState));
    }

    public OutputTransition getOutputTransition(String input){
        return this.transitions.getOrDefault(input, null);
    }

    public String getName(){
        return this.stateName;
    }

    public List<String> getInputs(){
        return new ArrayList<>(transitions.keySet());
    }

}
