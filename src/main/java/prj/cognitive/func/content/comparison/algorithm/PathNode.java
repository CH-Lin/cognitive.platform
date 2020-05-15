package prj.cognitive.func.content.comparison.algorithm;

public final class PathNode {

	public final int i;
	public final int j;
	public final PathNode prev;
	public final boolean snake;
	public final boolean bootstrap;

	public PathNode(int i, int j, boolean snake, boolean bootstrap, PathNode prev) {
		this.i = i;
		this.j = j;
		this.bootstrap = bootstrap;
		if (snake) {
			this.prev = prev;
		} else {
			this.prev = prev == null ? null : prev.previousSnake();
		}
		this.snake = snake;
	}

	public boolean isSnake() {
		return snake;
	}

	public boolean isBootstrap() {
		return bootstrap;
	}

	public final PathNode previousSnake() {
		if (isBootstrap()) {
			return null;
		}
		if (!isSnake() && prev != null) {
			return prev.previousSnake();
		}
		return this;
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder("[");
		PathNode node = this;
		while (node != null) {
			buf.append("(");
			buf.append(node.i);
			buf.append(",");
			buf.append(node.j);
			buf.append(")");
			node = node.prev;
		}
		buf.append("]");
		return buf.toString();
	}
}
