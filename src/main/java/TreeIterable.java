import it.units.malelab.jgea.representation.tree.Tree;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;


public class TreeIterable implements Iterable<Tree<Integer>> {

    private static class TreeIterator implements Iterator<Tree<Integer>> {

        private final Queue<Tree<Integer>> queue;

        public TreeIterator(Tree<Integer> root) {
            this.queue = new LinkedList<>();
            this.queue.add(root);
        }

        @Override
        public boolean hasNext() {
            return !this.queue.isEmpty();
        }

        @Override
        public Tree<Integer> next() {
            Tree<Integer> next = this.queue.remove();
            next.forEach(this.queue::add);
            return next;
        }

    }

    private final TreeIterator iterator;

    public TreeIterable(Tree<Integer> root) { this.iterator = new TreeIterator(root); }

    @Override
    public Iterator<Tree<Integer>> iterator() {
        return this.iterator;
    }

}
