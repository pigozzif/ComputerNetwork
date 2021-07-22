import it.units.malelab.jgea.core.operator.Mutation;
import it.units.malelab.jgea.representation.tree.Tree;

import java.util.*;


public class MoveOperator implements Mutation<Tree<TreeContent>> {

    private static class Move {

        private Tree<TreeContent> source;
        private Tree<TreeContent> oldParent;
        private Tree<TreeContent> newParent;
        private Tree<TreeContent> root;

        public Move(Tree<TreeContent> s, Tree<TreeContent> o, Tree<TreeContent> n, Tree<TreeContent> r) {
            this.source = s;
            this.oldParent = o;
            this.newParent = n;
            this.root = r;
        }

        public void apply() {
            this.oldParent.removeChild(this.source);
            this.newParent.addChild(this.source);
            this.updateShortestPaths();
        }

        private void updateShortestPaths() {
            for (Tree<TreeContent> node : new TreeIterable(this.root)) {
                node.content().setPathToCenter(ComputerNetworkFitnessFunction.getShortestPath(node)/*node.parent().content().getPathToCenter() +
                        ComputerNetworkProblem.getDistance(node.content().getIndex(), node.parent().content().getIndex())*/);
            }
        }

    }

    @Override
    public Tree<TreeContent> mutate(Tree<TreeContent> tree, Random random) {
        Tree<TreeContent> newBorn = TreeContent.copyTree(tree);
        List<Move> moves = getNeighbourhood(newBorn);
        Move move = moves.get(random.nextInt(moves.size()));
        move.apply();
        return newBorn;
    }

    public static List<Move> getNeighbourhood(Tree<TreeContent> tree) {
        List<Move> ans = new ArrayList<>();
        for (Tree<TreeContent> node : new TreeIterable(tree)) {
            if (node.parent() != null) {
                for (int i = 0; i < node.parent().nChildren(); ++i) {
                    Tree<TreeContent> child = node.parent().child(i);
                    if (!child.equals(node)) {
                        ans.add(new Move(node, node.parent(), child, tree));
                    }
                }
                if (node.parent().parent() != null) {
                    ans.add(new Move(node, node.parent(), node.parent().parent(), tree));
                }
            }
        }
        return ans;
    }

}
