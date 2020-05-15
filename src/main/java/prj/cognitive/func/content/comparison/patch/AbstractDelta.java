package prj.cognitive.func.content.comparison.patch;

import java.util.Objects;

public abstract class AbstractDelta<T> {
	private final Chunk<T> source;
	private final Chunk<T> target;
	private final DeltaType type;

	public AbstractDelta(DeltaType type, Chunk<T> source, Chunk<T> target) {
		Objects.requireNonNull(source);
		Objects.requireNonNull(target);
		Objects.requireNonNull(type);
		this.type = type;
		this.source = source;
		this.target = target;
	}

	public Chunk<T> getSource() {
		return source;
	}

	public Chunk<T> getTarget() {
		return target;
	}

	public DeltaType getType() {
		return type;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.source, this.target, this.type);
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
		final AbstractDelta<?> other = (AbstractDelta<?>) obj;
		if (!Objects.equals(this.source, other.source)) {
			return false;
		}
		if (!Objects.equals(this.target, other.target)) {
			return false;
		}
		if (this.type != other.type) {
			return false;
		}
		return true;
	}
}
