package fuzzing;

import mealyMachine.MealyState;
import sut.MQTTClientWrapper;

import java.io.IOException;

public interface FuzzingMapper {

    /**
     * @param methodInput abstract input, one found in learning alphabet/automata inputs
     * @param client MQTT client wrapper with already set up configuration, used to execute commands
     * @return pair, first value being a String which was used for fuzzing, other output is the return value of the executed method
     * @throws IOException
     */
    FuzzOutputPair fuzzInputSymbol(MealyState state, String methodInput, MQTTClientWrapper client) throws IOException;

    void clearMapperData();
}
