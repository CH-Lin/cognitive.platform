package prj.cognitive.func.content.comparison.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.diff.Sequence;
import org.eclipse.jgit.diff.SequenceComparator;

import prj.cognitive.func.content.comparison.patch.DeltaType;

public class HistogramDiff<T> implements DiffAlgorithmI<T> {

	@Override
	public List<Change> computeDiff(List<T> source, List<T> target) {
		Objects.requireNonNull(source, "source list must not be null");
		Objects.requireNonNull(target, "target list must not be null");
		EditList diffList = new EditList();
		diffList.addAll(new org.eclipse.jgit.diff.HistogramDiff().diff(new DataListComparator<>(),
				new DataList<>(source), new DataList<>(target)));
		List<Change> patch = new ArrayList<>();
		for (Edit edit : diffList) {
			DeltaType type = DeltaType.EQUAL;
			switch (edit.getType()) {
			case DELETE:
				type = DeltaType.DELETE;
				break;
			case INSERT:
				type = DeltaType.INSERT;
				break;
			case REPLACE:
				type = DeltaType.CHANGE;
				break;
			case EMPTY:
				// Do nothing
				break;
			}
			patch.add(new Change(type, edit.getBeginA(), edit.getEndA(), edit.getBeginB(), edit.getEndB()));
		}
		return patch;
	}
}

class DataListComparator<T> extends SequenceComparator<DataList<T>> {

	public DataListComparator() {
	}

	@Override
	public boolean equals(DataList<T> original, int orgIdx, DataList<T> revised, int revIdx) {
		return original.data.get(orgIdx).equals(revised.data.get(revIdx));
	}

	@Override
	public int hash(DataList<T> s, int i) {
		return s.data.get(i).hashCode();
	}
}

class DataList<T> extends Sequence {

	final List<T> data;

	public DataList(List<T> data) {
		this.data = data;
	}

	@Override
	public int size() {
		return data.size();
	}
}
