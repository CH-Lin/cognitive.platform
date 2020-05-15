package prj.cognitive.func.content.comparison.patch;

import java.util.Objects;

public final class ChangeDelta<T> extends AbstractDelta<T> {

	public ChangeDelta(Chunk<T> source, Chunk<T> target) {
		super(DeltaType.CHANGE, source, target);
		Objects.requireNonNull(source, "source must not be null");
		Objects.requireNonNull(target, "target must not be null");
	}

	@Override
	public String toString() {
		return "[ChangeDelta, position: " + getSource().getPosition() + ", from: " + getSource().getLines() + " to "
				+ getTarget().getLines() + "]";
	}
}
