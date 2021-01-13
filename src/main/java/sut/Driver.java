package sut;

import de.learnlib.drivers.api.TestDriver;
import de.learnlib.drivers.reflect.ConcreteMethodInput;
import de.learnlib.mapper.api.SULMapper;
import net.automatalib.words.Alphabet;
import net.automatalib.words.impl.SimpleAlphabet;

import java.util.List;

public class Driver extends TestDriver<String, String, ConcreteMethodInput, Object>{
    private SimpleAlphabet<String> alphabet = new SimpleAlphabet<>();

    public Driver(SULMapper<String, String, ConcreteMethodInput, Object> mapper) {
        super(mapper);
    }

    public void addAlphabet(List<String> inputs){
        alphabet.addAll(inputs);
    }

    public Alphabet<String> getAlphabet(){ return this.alphabet;}

}
