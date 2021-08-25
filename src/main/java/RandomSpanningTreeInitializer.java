import it.units.malelab.jgea.core.Factory;
import it.units.malelab.jgea.core.IndependentFactory;
import it.units.malelab.jgea.representation.tree.Tree;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public class RandomSpanningTreeInitializer implements Factory<Tree<Integer>> {

    public static class IndependentRandomSpanningTreeInitializer implements IndependentFactory<Tree<Integer>> {

        private final int nBuildings;

        public IndependentRandomSpanningTreeInitializer(int n) {
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
                } else {
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

        private Tree<Integer> convertArrayToTree(int[] parents) {
            Map<Integer, Tree<Integer>> parentMap = new HashMap<>();
            Queue<Integer> queue = new LinkedList<>();
            queue.add(parents.length - 1);
            Tree<Integer> root = null;
            while (!queue.isEmpty()) {
                int idx = queue.remove();
                Tree<Integer> node = Tree.of(idx);
                if (root == null) {
                    root = node;
                } else {
                    parentMap.get(parents[idx]).addChild(node);
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
        public Tree<Integer> build(Random random) {
            int[] parents = this.randomSpanningTree(random);
            return this.convertArrayToTree(parents);
        }

    }

    private final int nInit;
    private final IndependentRandomSpanningTreeInitializer independentFactory;
    private final Function<Tree<Integer>, Double> f;

    public RandomSpanningTreeInitializer(int nBuildings, int nInit, Function<Tree<Integer>, Double> f) {
        this.nInit = nInit;
        this.independentFactory = new IndependentRandomSpanningTreeInitializer(nBuildings);
        this.f = f;
    }

    @Override
    public List<Tree<Integer>> build(int n, Random random) {
        return this.independentFactory.build(this.nInit, random).stream().sorted(Comparator.comparing(this.f)).collect(Collectors.toList()).subList(0, n);
    }

}
