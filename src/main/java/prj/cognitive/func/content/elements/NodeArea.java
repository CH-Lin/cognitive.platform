package prj.cognitive.func.content.elements;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

public class NodeArea implements Comparable<NodeArea> {

	protected double x;
	protected double y;
	protected double width;
	protected double height;

	public NodeArea(double x, double y, double w, double h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public int compareTo(NodeArea o) {
		return ComparisonChain.start().compare(this.x, o.x).compare(this.y, o.y).compare(this.width, o.width)
				.compare(this.height, o.height).result();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof NodeArea)) {
			return false;
		}
		NodeArea node = (NodeArea) o;
		return (this == o)
				|| (this.x == node.x && this.y == node.y && this.width == node.width && this.height == node.height);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.x, this.y, this.width, this.height);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("x", this.x).add("y", this.y).add("width", this.width)
				.add("height", this.height).toString();
	}
}
