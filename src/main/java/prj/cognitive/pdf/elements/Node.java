package prj.cognitive.pdf.elements;

import prj.cognitive.pdf.elements.attrs.PositionalArea;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class Node implements Comparable<Node> {
    @Expose
    protected List<Node> children = new ArrayList<>();

    protected Node parent;

    @Expose
    protected PositionalArea shape;

    public Node() {

    }

    public Node(Node parent) {
        this.parent = parent;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public void appendChild(Node child) {
        child.setParent(this);
        children.add(child);
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public PositionalArea getShape() {
        return shape;
    }

    public void setShape(PositionalArea shape) {
        this.shape = shape;
    }

    public int compareTo(Node that) {
        return ComparisonChain.start()
                .compare(this.shape, that.shape)
                .result();
    }

    public void limitDepth(int depth) {
        if (depth > 0) {
            children.forEach(child -> child.limitDepth(depth - 1));
        } else {
            children = null;
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("children", children)
                .add("shape", shape)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;
        Node node = (Node) o;
        return Objects.equal(children, node.children) &&
                Objects.equal(parent, node.parent) &&
                Objects.equal(shape, node.shape);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(children, parent, shape);
    }
}
