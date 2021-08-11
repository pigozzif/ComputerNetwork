import it.units.malelab.jgea.core.IndependentFactory;
import it.units.malelab.jgea.representation.tree.Tree;

import java.util.*;


public class RandomSpanningTreeInitializer implements IndependentFactory<Tree<TreeContent>> {

    private final int nBuildings;

    public RandomSpanningTreeInitializer(int n) {
        this.nBuildings = n;
    }

    private int[] randomSpanningTree(Random random) {
        int n = this.nBuildings - 2;
        int[] c = new int[n];
        int[] d = new int[this.nBuildings];
        Arrays.fill(d, 0);
        for (int i = 0; i < n; ++i) {
            final int v = random.nextInt(this.nBuildings);
            c[i] = v;
            ++d[v];
        }
        int i = 0;
        while (d[i] != 0) {
            ++i;
        }
        int leaf = i;
        int[] parents = new int[this.nBuildings];
        Arrays.fill(parents, -1);
        for (int j = 0; j < n; ++j) {
            final int v = c[j];
            parents[leaf] = v;
            if (--d[v] == 0 && v < i) {
                leaf = v;
            }
            else {
                ++i;
                while (d[i] != 0) {
                    ++i;
                }
                leaf = i;
            }
        }
        parents[leaf] = this.nBuildings - 1;
        return parents;
    }

    private Tree<TreeContent> convertArrayToTree(int[] parents) {
        Map<Integer, Tree<TreeContent>> parentMap = new HashMap<>();
        Queue<Integer> queue = new LinkedList<>();
        queue.add(parents.length - 1);
        Tree<TreeContent> root = null;
        while (!queue.isEmpty()) {
            int idx = queue.remove();
            Tree<TreeContent> node = Tree.of(new TreeContent(idx));
            if (root == null) {
                root = node;
            }
            else {
                Tree<TreeContent> parent = parentMap.get(parents[idx]);
                parent.addChild(node);
                node.content().setPathToCenter(parent.content().getPathToCenter() +
                        ComputerNetworkProblem.getDistance(parent.content().getIndex(), node.content().getIndex()));
            }
            parentMap.put(idx, node);
            for (int i = 0; i < parents.length; ++i) {
                if (parents[i] == idx) {
                    queue.add(i);
                }
            }
        }
        return root;
    }

    @Override
    public Tree<TreeContent> build(Random random) {
        int[] parents = this.randomSpanningTree(random);
        return this.convertArrayToTree(parents);
    }

}
