import it.units.malelab.jgea.core.operator.Mutation;
import it.units.malelab.jgea.representation.tree.Tree;

import java.util.*;


public class MoveOperator implements Mutation<Tree<Integer>> {

    private static class Move {

        public Tree<Integer> source;
        public Tree<Integer> oldParent;
        public Tree<Integer> newParent;

        public Move(Tree<Integer> s, Tree<Integer> o, Tree<Integer> n) {
            this.source = s;
            this.oldParent = o;
            this.newParent = n;
        }

        public void apply() {
            this.oldParent.removeChild(this.source);
            this.newParent.addChild(this.source);
        }

    }

    @Override
    public Tree<Integer> mutate(Tree<Integer> tree, Random random) {
        Tree<Integer> newBorn = Tree.copyOf(tree);
        List<Move> moves = getNeighbourhood(newBorn);
        Move move = moves.get(random.nextInt(moves.size()));
        move.apply();
        return newBorn;
    }

    public static List<Move> getNeighbourhood(Tree<Integer> tree) {
        List<Move> ans = new ArrayList<>();
        for (Tree<Integer> node : new TreeIterable(tree)) {
            if (node.parent() != null) {
                for (int i = 0; i < node.parent().nChildren(); ++i) {
                    Tree<Integer> child = node.parent().child(i);
                    if (!child.equals(node)) {
                        ans.add(new Move(node, node.parent(), child));
                    }
                }
                if (node.parent().parent() != null) {
                    ans.add(new Move(node, node.parent(), node.parent().parent()));
                }
            }
        }
        return ans;
    }

}