package prj.cognitive.func.content.comparison.patch;

public final class DeleteDelta<T> extends AbstractDelta<T> {

	public DeleteDelta(Chunk<T> original, Chunk<T> revised) {
		super(DeltaType.DELETE, original, revised);
	}

	@Override
	public String toString() {
		return "[DeleteDelta, position: " + getSource().getPosition() + ", from: " + getSource().getLines() + "]";
	}
}
