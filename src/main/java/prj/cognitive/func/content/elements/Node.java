package prj.cognitive.func.content.elements;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ComparisonChain;

import java.util.ArrayList;
import java.util.List;

public class Node implements Comparable<Node> {
	protected List<Node> children = new ArrayList<>();

	protected Node parent;

	protected NodeArea area;

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

	public void addChild(Node child) {
		child.setParent(this);
		children.add(child);
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public NodeArea getArea() {
		return area;
	}

	public void setArea(NodeArea area) {
		this.area = area;
	}

	public void setArea(float x, float y, float w, float h) {
		this.area = new NodeArea(x, y, w, h);
	}

	public int compareTo(Node that) {
		return ComparisonChain.start().compare(this.area, that.area).result();
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("children", children).add("area", area).toString();
	}

}
