package prj.cognitive.func.content.comparison.patch;

import static java.util.Comparator.comparing;

import prj.cognitive.func.content.comparison.algorithm.Change;
import prj.cognitive.func.content.result.Result;

import java.util.ArrayList;
import java.util.List;

public final class DiffResult<T> extends Result {

	private final List<AbstractDelta<T>> deltas;

	public DiffResult() {
		this(10);
	}

	public DiffResult(int estimatedPatchSize) {
		deltas = new ArrayList<>(estimatedPatchSize);
	}

	public void addDelta(AbstractDelta<T> delta) {
		deltas.add(delta);
	}

	public List<AbstractDelta<T>> getDeltas() {
		deltas.sort(comparing(d -> d.getSource().getPosition()));
		return deltas;
	}

	public static <T> DiffResult<T> generate(List<T> original, List<T> revised, List<Change> changes) {
		DiffResult<T> patch = new DiffResult<>(changes.size());
		for (Change change : changes) {
			Chunk<T> orgChunk = new Chunk<>(change.startOriginal,
					new ArrayList<>(original.subList(change.startOriginal, change.endOriginal)));
			Chunk<T> revChunk = new Chunk<>(change.startRevised,
					new ArrayList<>(revised.subList(change.startRevised, change.endRevised)));
			switch (change.deltaType) {
			case DELETE:
				patch.addDelta(new DeleteDelta<>(orgChunk, revChunk));
				break;
			case INSERT:
				patch.addDelta(new InsertDelta<>(orgChunk, revChunk));
				break;
			case CHANGE:
				patch.addDelta(new ChangeDelta<>(orgChunk, revChunk));
				break;
			case EQUAL:
				// Do nothing
				break;
			}
		}
		return patch;
	}

	@Override
	public String toString() {
		return "Patch{" + "deltas=" + deltas + '}';
	}
}
