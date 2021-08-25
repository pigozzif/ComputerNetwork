import it.units.malelab.jgea.representation.tree.Tree;

import java.util.function.Function;


public class ComputerNetworkFitnessFunction implements Function<Tree<TreeContent>, Double> {

    private final ComputerNetworkProblem problem;

    public ComputerNetworkFitnessFunction(ComputerNetworkProblem p) {
        this.problem = p;
    }

    public static double getShortestPath(Tree<TreeContent> node) {
        double cost = 0.0;
        Tree<TreeContent> currentNode = node;
        while (currentNode.parent() != null) {
            cost += ComputerNetworkProblem.getDistance(currentNode.content().getIndex(), currentNode.parent().content().getIndex());
            currentNode = currentNode.parent();
        }
        return cost;
    }

    @Override
    public Double apply(Tree<TreeContent> tree) {
        double fitness = 0.0;
        for (Tree<TreeContent> node : new TreeIterable(tree)) {
            if (node.parent() != null) {
                fitness += problem.getTrenchCost() * ComputerNetworkProblem.getDistance(node.content().getIndex(), node.parent().content().getIndex());
                fitness += problem.getCableCost() * node.content().getPathToCenter();
            }
        }
        return fitness;
    }

}
