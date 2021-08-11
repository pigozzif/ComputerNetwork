import it.units.malelab.jgea.Worker;
import it.units.malelab.jgea.core.Individual;
import it.units.malelab.jgea.core.evolver.Event;
import it.units.malelab.jgea.core.evolver.Evolver;
import it.units.malelab.jgea.core.evolver.StandardEvolver;
import it.units.malelab.jgea.core.evolver.stopcondition.Iterations;
import it.units.malelab.jgea.core.listener.*;
import it.units.malelab.jgea.core.order.PartialComparator;
import it.units.malelab.jgea.core.selector.Tournament;
import it.units.malelab.jgea.core.selector.Worst;
import it.units.malelab.jgea.core.util.Args;
import it.units.malelab.jgea.core.util.Misc;
import it.units.malelab.jgea.representation.tree.Tree;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import static it.units.malelab.jgea.core.listener.NamedFunctions.*;


public class Main extends Worker {

    private static String inputFileName = System.getProperty("user.dir") + "/data/";
    private static String outputFileName = System.getProperty("user.dir") + "/output/";
    private static String seed;

    public Main(String[] args) {
        super(args);
    }

    public static void main(String[] args) {
        seed = Args.a(args, "seed", null);
        String input = Args.a(args, "input", null);
        inputFileName += input + ".txt";
        outputFileName += input + "/" + seed + ".txt";
        new Main(args);
    }

    @Override
    public void run() {
        try {
            this.evolve();
        } catch (IOException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void evolve() throws IOException, ExecutionException, InterruptedException {
        Listener.Factory<Event<?, ?, ? extends Double>> factory = this.createListenerFactory();
        ComputerNetworkProblem problem = new ComputerNetworkProblem(inputFileName, 1.0, 1.0);
        Evolver<Tree<TreeContent>, Tree<TreeContent>, Double> evolver = new StandardEvolver<>(Function.identity(),
                new RandomSpanningTreeInitializer(problem.getNBuildings()),
                PartialComparator.from(Double.class).comparing(Individual::getFitness),
                200,
                Map.of(new MoveOperator(), 1.0),
                new Tournament(5),
                new Worst(),
                200,
                true,
                false);
        Collection<Tree<TreeContent>> solutions = evolver.solve(problem.getFitnessFunction(),
                new Iterations(1000),
                new Random(Integer.parseInt(seed)),
                this.executorService,
                factory.build());
        factory.shutdown();
    }

    private Listener.Factory<Event<?, ?, ? extends Double>> createListenerFactory() {
        return new CSVPrinter<>(Misc.concat(List.of(
                List.of(iterations(),
                births(),
                elapsedSeconds(),
                uniqueness().of(each(genotype())).of(all()),
                uniqueness().of(each(solution())).of(all()),
                uniqueness().of(each(fitness())).of(all())),
                List.of(fitness().reformat("%5.3f").of(best())),
                List.of(f("print", x -> ((Individual<Tree<TreeContent>, Tree<TreeContent>, Double>) x).getSolution().toString()).of(best())))), new File(outputFileName));
    }

}
