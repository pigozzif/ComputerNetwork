import it.units.malelab.jgea.Worker;
import it.units.malelab.jgea.core.Individual;
import it.units.malelab.jgea.core.evolver.*;
import it.units.malelab.jgea.core.evolver.stopcondition.Iterations;
import it.units.malelab.jgea.core.listener.*;
import it.units.malelab.jgea.core.order.PartialComparator;
import it.units.malelab.jgea.core.selector.Elitism;
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
    private static String evolverName;

    public Main(String[] args) {
        super(args);
    }

    public static void main(String[] args) {
        seed = Args.a(args, "seed", null);
        evolverName = Args.a(args, "evolver", null);
        String input = Args.a(args, "input", null);
        inputFileName += input + ".txt";
        outputFileName += String.join(".", input, String.valueOf(seed), evolverName, "txt");
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
        Evolver<Tree<Integer>, Tree<Integer>, Double> evolver = switch (evolverName) {
            case "ga" -> createGAEvolver(problem.getNBuildings());
            case "random" -> createRandomSearchEvolver(problem.getNBuildings());
            case "shd_first_improvement" -> createSHDEvolver(problem.getNBuildings(), 1, 1, 1, problem.getFitnessFunction());
            case "shd_1_plus_lambda" -> createSHDEvolver(problem.getNBuildings(), 1, 100, 1 + 100, problem.getFitnessFunction());
            case "shd_mu_plus_lambda" -> createSHDEvolver(problem.getNBuildings(), 100, 100, 200, problem.getFitnessFunction());
            default -> throw new IllegalArgumentException("Unknown evolver name: " + evolverName);
        };
        Collection<Tree<Integer>> solutions = evolver.solve(problem.getFitnessFunction(),
                new Iterations(1000),
                new Random(Integer.parseInt(seed)),
                this.executorService,
                factory.build());
        factory.shutdown();
    }

    private Evolver<Tree<Integer>, Tree<Integer>, Double> createGAEvolver(int nBuildings) {
        int popSize = 200;
        return new StandardEvolver<>(Function.identity(),
                new RandomSpanningTreeInitializer.IndependentRandomSpanningTreeInitializer(nBuildings),
                PartialComparator.from(Double.class).comparing(Individual::getFitness),
                popSize,
                Map.of(new MoveOperator(), 1.0),
                new Tournament(5),
                new Worst(),
                popSize,
                true,
                false);
    }

    private Evolver<Tree<Integer>, Tree<Integer>, Double> createRandomSearchEvolver(int nBuildings) {
        return new RandomSearch<>(Function.identity(),
                new RandomSpanningTreeInitializer.IndependentRandomSpanningTreeInitializer(nBuildings),
                PartialComparator.from(Double.class).comparing(Individual::getFitness));
    }

    private Evolver<Tree<Integer>, Tree<Integer>, Double> createSHDEvolver(int nBuildings, int mu, int lambda, int nInit, Function<Tree<Integer>, Double> f) {
        return new StandardEvolver<>(Function.identity(),
                new RandomSpanningTreeInitializer(nBuildings, nInit, f),
                PartialComparator.from(Double.class).comparing(Individual::getFitness),
                mu,
                Map.of(new MoveOperator(), 1.0),
                new Elitism(),
                new Worst(),
                lambda,
                true,
                false);
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
                List.of(f("print", x -> ((Individual<Tree<Integer>, Tree<Integer>, Double>) x).getSolution().toString()).of(best())))), new File(outputFileName));
    }

}
