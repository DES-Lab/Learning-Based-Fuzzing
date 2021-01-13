package mealyMachine;

public class OutputTransition{
    public String output;
    public MealyState state;

    public OutputTransition(String output, MealyState state){
        this.output = output;
        this.state = state;
    }
}
