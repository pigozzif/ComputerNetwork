import it.units.malelab.jgea.core.Problem;
import it.units.malelab.jgea.representation.tree.Tree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


public class ComputerNetworkProblem implements Problem<Tree<Integer>, Double> {

    private final double trenchCost;
    private final double cableCost;
    private int nBuildings;
    private final double[][] distances;
    private final Function<Tree<Integer>, Double> fitnessFunction;

    public ComputerNetworkProblem(String filename, double tc, double cc) throws IOException {
        this.trenchCost = tc;
        this.cableCost = cc;
        List<Integer> coordinatesX = new ArrayList<>();
        List<Integer> coordinatesY = new ArrayList<>();

        // read buildings' coordinates from file
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String row;
        while ((row = reader.readLine()) != null) {
            String[] data = row.split(",");
            coordinatesX.add(Integer.parseInt(data[0]));
            coordinatesY.add(Integer.parseInt(data[1]));
            ++this.nBuildings;
        }
        reader.close();

        // compute distance matrix
        this.distances = new double[this.nBuildings][this.nBuildings];
        for (int i = 0; i < this.nBuildings; ++i) {
            for (int j = 0; j < this.nBuildings; ++j) {
                distances[i][j] = Math.sqrt(Math.pow(coordinatesX.get(i) - coordinatesX.get(j), 2) + Math.pow(coordinatesY.get(i) - coordinatesY.get(j), 2));
            }
        }

        this.fitnessFunction = new ComputerNetworkFitnessFunction(this);
    }

    public int getNBuildings() { return this.nBuildings; }

    public double getTrenchCost() { return this.trenchCost; }

    public double getCableCost() { return this.cableCost; }

    public double getDistance(int i, int j) { return this.distances[i][j]; }

    @Override
    public Function<Tree<Integer>, Double> getFitnessFunction() { return this.fitnessFunction; }

}
