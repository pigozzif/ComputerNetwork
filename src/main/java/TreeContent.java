import it.units.malelab.jgea.representation.tree.Tree;

import java.util.Objects;

public class TreeContent {

    private final int index;
    private double pathToCenter;

    public TreeContent(int idx) {
        this(idx, 0.0);
    }

    public TreeContent(int idx, double p) {
        this.index = idx;
        this.pathToCenter = p;
    }

    public int getIndex() { return this.index; }

    public double getPathToCenter() { return this.pathToCenter; }

    public void setPathToCenter(double p) { this.pathToCenter = p; }

    public static Tree<TreeContent> copyTree(Tree<TreeContent> other) {
        Tree<TreeContent> t = Tree.of(new TreeContent(other.content().getIndex(), other.content().getPathToCenter()));
        for (Tree<TreeContent> child : other) {
            t.addChild(copyTree(child));
        }
        return t;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TreeContent that = (TreeContent) o;
        return index == that.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }

}
