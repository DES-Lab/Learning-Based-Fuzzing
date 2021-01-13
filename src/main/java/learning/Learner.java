package learning;

import de.learnlib.acex.analyzers.AcexAnalyzers;
import de.learnlib.algorithms.lstar.ce.ObservationTableCEXHandlers;
import de.learnlib.algorithms.lstar.closing.ClosingStrategies;
import de.learnlib.algorithms.lstar.mealy.ExtensibleLStarMealy;
import de.learnlib.algorithms.ttt.mealy.TTTLearnerMealy;
import de.learnlib.api.SUL;
import de.learnlib.api.oracle.EquivalenceOracle;
import de.learnlib.api.statistic.StatisticSUL;
import de.learnlib.drivers.reflect.ConcreteMethodInput;
import de.learnlib.filter.cache.sul.SULCaches;
import de.learnlib.filter.statistic.sul.ResetCounterSUL;
import de.learnlib.mapper.api.SULMapper;
import de.learnlib.oracle.equivalence.mealy.RandomWalkEQOracle;
import de.learnlib.oracle.membership.SULOracle;
import de.learnlib.util.Experiment;
import de.learnlib.util.statistics.SimpleProfiler;
import net.automatalib.visualization.Visualization;
import sut.Driver;
import net.automatalib.automata.transducers.MealyMachine;
import net.automatalib.serialization.dot.GraphDOT;
import net.automatalib.words.Word;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Learner {
    private SULMapper<String, String, ConcreteMethodInput, Object> mapper;

    public Learner(SULMapper<String, String, ConcreteMethodInput, Object> mapper){
        this.mapper = mapper;
    }

    public Experiment.MealyExperiment<String, String> learn(int numSteps, String experimentName, List<String> inputAlphabet) throws IOException, NoSuchMethodException, InterruptedException {
        double resetProbability = 0.09;

        Driver driver = new Driver(mapper);

        // 2 clients - publisher and reader - to avoid non determinism
        // authentication
        driver.addAlphabet(inputAlphabet);

        List<Word<String>> initialSuffixes = new ArrayList<>();
        driver.getAlphabet().forEach(it -> {initialSuffixes.add(Word.fromSymbols(it));});

        StatisticSUL<String , String> statisticSul = new ResetCounterSUL<>("membership queries", driver);
        SUL<String, String> effectiveSul = statisticSul;
        effectiveSul = SULCaches.createCache(driver.getAlphabet(), effectiveSul);

        SULOracle<String, String> mqOracle = new SULOracle<>(effectiveSul);
        TTTLearnerMealy<String, String> ttt = new TTTLearnerMealy<>(driver.getAlphabet(), mqOracle, AcexAnalyzers.BINARY_SEARCH_BWD);
        ExtensibleLStarMealy<String, String> lStarMealy = new ExtensibleLStarMealy<>(driver.getAlphabet(), mqOracle, initialSuffixes, ObservationTableCEXHandlers.RIVEST_SCHAPIRE, ClosingStrategies.CLOSE_SHORTEST);

        EquivalenceOracle.MealyEquivalenceOracle<String, String> eqOracle = new RandomWalkEQOracle<>(driver, // system under learning
                resetProbability, // reset SUL w/ this probability before a step
                numSteps, // max steps (overall)
                true, // reset step count after counterexample
                new Random(18021996) // make results reproducible
        );
//        EquivalenceOracle.MealyEquivalenceOracle<String, String> eqOracle = new ExtendedEqOracle<>(driver, resetProbability, numSteps, null, null);

        Experiment.MealyExperiment<String, String> experiment = new Experiment.MealyExperiment<>(lStarMealy, eqOracle, driver.getAlphabet());
        experiment.setProfile(true);
        experiment.setLogModels(true);
        experiment.run();

        System.out.println(SimpleProfiler.getResults());
        System.out.println(experiment.getRounds().getSummary());
        System.out.println(statisticSul.getStatisticalData().getSummary());


        MealyMachine<?, String, ?, String> result = experiment.getFinalHypothesis();

        // model statistics
        System.out.println("States: " + result.size());
        System.out.println("Sigma: " + driver.getAlphabet().size());

        System.out.println("Model: ");
        String filepath = "learnedModels/" + experimentName + ".dot";
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(filepath));
        GraphDOT.write(result, driver.getAlphabet(), outputStreamWriter); // may throw IOException!
        outputStreamWriter.flush();
        outputStreamWriter.close();
        System.out.println("Model written to " + filepath);
        //LearningUtil.deleteSSTandVizualize(filepath);
        //Visualization.visualize(result, driver.getInputs());
        return experiment;
    }

}
