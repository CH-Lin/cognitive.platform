package prj.cognitive.func.content.comparison.patch;

public final class InsertDelta<T> extends AbstractDelta<T> {

	public InsertDelta(Chunk<T> original, Chunk<T> revised) {
		super(DeltaType.INSERT, original, revised);
	}

	@Override
	public String toString() {
		return "[InsertDelta, position: " + getSource().getPosition() + ", from: " + getTarget().getLines() + "]";
	}
}
