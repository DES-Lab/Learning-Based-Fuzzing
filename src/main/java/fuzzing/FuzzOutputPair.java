package fuzzing;


public class FuzzOutputPair {
    private String fuzzedString;
    private String output;

    public FuzzOutputPair(String fuzzedString, String output) {
        this.fuzzedString = fuzzedString;
        this.output = output;
    }

    public FuzzOutputPair() {
    }

    public String getFuzzedString() {
        return fuzzedString;
    }

    public void setFuzzedString(String fuzzedString) {
        this.fuzzedString = fuzzedString;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
