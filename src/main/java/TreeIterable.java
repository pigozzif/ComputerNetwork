import it.units.malelab.jgea.representation.tree.Tree;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;


public class TreeIterable implements Iterable<Tree<TreeContent>> {

    private static class TreeIterator implements Iterator<Tree<TreeContent>> {

        private final Queue<Tree<TreeContent>> queue;

        public TreeIterator(Tree<TreeContent> root) {
            this.queue = new LinkedList<>();
            this.queue.add(root);
        }

        @Override
        public boolean hasNext() {
            return !this.queue.isEmpty();
        }

        @Override
        public Tree<TreeContent> next() {
            Tree<TreeContent> next = this.queue.remove();
            for (int i = 0; i < next.nChildren(); ++i) {
                this.queue.add(next.child(i));
            }
            return next;
        }

    }

    private final TreeIterator iterator;

    public TreeIterable(Tree<TreeContent> root) { this.iterator = new TreeIterator(root); }

    @Override
    public Iterator<Tree<TreeContent>> iterator() {
        return this.iterator;
    }

}
