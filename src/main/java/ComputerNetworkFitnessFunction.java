import it.units.malelab.jgea.representation.tree.Tree;

import java.util.function.Function;


public class ComputerNetworkFitnessFunction implements Function<Tree<Integer>, Double> {

    private final ComputerNetworkProblem problem;

    public ComputerNetworkFitnessFunction(ComputerNetworkProblem p) {
        this.problem = p;
    }

    private double getShortestPath(Tree<Integer> node) {
        double cost = 0.0;
        Tree<Integer> currentNode = node;
        while (currentNode.parent() != null) {
            cost += this.problem.getDistance(currentNode.content(), currentNode.parent().content());
            currentNode = currentNode.parent();
        }
        return cost;
    }

    @Override
    public Double apply(Tree<Integer> tree) {
        double fitness = 0.0;
        for (Tree<Integer> node : new TreeIterable(tree)) {
            if (node.parent() != null) {
                fitness += problem.getTrenchCost() * this.problem.getDistance(node.content(), node.parent().content());
                fitness += problem.getCableCost() * this.getShortestPath(node);
            }
        }
        return fitness;
    }

}