package prj.cognitive.func.content.comparison.patch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class Chunk<T> {

	private final int position;
	private List<T> lines;

	public Chunk(int position, List<T> lines) {
		this.position = position;
		this.lines = new ArrayList<>(lines);
	}

	public Chunk(int position, T[] lines) {
		this.position = position;
		this.lines = Arrays.asList(lines);
	}

	public int getPosition() {
		return position;
	}

	public List<T> getLines() {
		return lines;
	}

	public int size() {
		return lines.size();
	}

	public int last() {
		return getPosition() + size() - 1;
	}

	@Override
	public int hashCode() {
		return Objects.hash(lines, position, size());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		@SuppressWarnings("unchecked")
		Chunk<T> other = (Chunk<T>) obj;
		if (lines == null) {
			if (other.lines != null) {
				return false;
			}
		} else if (!lines.equals(other.lines)) {
			return false;
		}
		return position == other.position;
	}

	@Override
	public String toString() {
		return "[position: " + position + ", size: " + size() + ", lines: " + lines + "]";
	}

}
