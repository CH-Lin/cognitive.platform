package prj.cognitive.func.content.comparison.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;

import prj.cognitive.func.content.comparison.patch.DeltaType;

public final class MyersDiff<T> implements DiffAlgorithmI<T> {

	private final BiPredicate<T, T> DEFAULT_EQUALIZER = Object::equals;
	private final BiPredicate<T, T> equalizer;

	public MyersDiff() {
		equalizer = DEFAULT_EQUALIZER;
	}

	public MyersDiff(final BiPredicate<T, T> equalizer) {
		Objects.requireNonNull(equalizer, "equalizer must not be null");
		this.equalizer = equalizer;
	}

	@Override
	public List<Change> computeDiff(final List<T> source, final List<T> target) throws DiffException {
		Objects.requireNonNull(source, "source list must not be null");
		Objects.requireNonNull(target, "target list must not be null");

		PathNode path = buildPath(source, target);
		List<Change> result = buildRevision(path, source, target);
		return result;
	}

	private PathNode buildPath(final List<T> orig, final List<T> rev) throws DifferentiationFailedException {
		Objects.requireNonNull(orig, "original sequence is null");
		Objects.requireNonNull(rev, "revised sequence is null");

		final int N = orig.size();
		final int M = rev.size();

		final int MAX = N + M + 1;
		final int size = 1 + 2 * MAX;
		final int middle = size / 2;
		final PathNode diagonal[] = new PathNode[size];

		diagonal[middle + 1] = new PathNode(0, -1, true, true, null);
		for (int d = 0; d < MAX; d++) {
			for (int k = -d; k <= d; k += 2) {
				final int kmiddle = middle + k;
				final int kplus = kmiddle + 1;
				final int kminus = kmiddle - 1;
				PathNode prev;
				int i;

				if ((k == -d) || (k != d && diagonal[kminus].i < diagonal[kplus].i)) {
					i = diagonal[kplus].i;
					prev = diagonal[kplus];
				} else {
					i = diagonal[kminus].i + 1;
					prev = diagonal[kminus];
				}

				diagonal[kminus] = null; // no longer used

				int j = i - k;

				PathNode node = new PathNode(i, j, false, false, prev);

				while (i < N && j < M && equalizer.test(orig.get(i), rev.get(j))) {
					i++;
					j++;
				}

				if (i != node.i) {
					node = new PathNode(i, j, true, false, node);
				}

				diagonal[kmiddle] = node;

				if (i >= N && j >= M) {
					return diagonal[kmiddle];
				}
			}
			diagonal[middle + d - 1] = null;
		}
		throw new DifferentiationFailedException("could not find a diff path");
	}

	private List<Change> buildRevision(PathNode actualPath, List<T> orig, List<T> rev) {
		Objects.requireNonNull(actualPath, "path is null");
		Objects.requireNonNull(orig, "original sequence is null");
		Objects.requireNonNull(rev, "revised sequence is null");

		PathNode path = actualPath;
		List<Change> changes = new ArrayList<>();
		if (path.isSnake()) {
			path = path.prev;
		}
		while (path != null && path.prev != null && path.prev.j >= 0) {
			if (path.isSnake()) {
				throw new IllegalStateException("bad diffpath: found snake when looking for diff");
			}
			int i = path.i;
			int j = path.j;

			path = path.prev;
			int ianchor = path.i;
			int janchor = path.j;

			if (ianchor == i && janchor != j) {
				changes.add(new Change(DeltaType.INSERT, ianchor, i, janchor, j));
			} else if (ianchor != i && janchor == j) {
				changes.add(new Change(DeltaType.DELETE, ianchor, i, janchor, j));
			} else {
				changes.add(new Change(DeltaType.CHANGE, ianchor, i, janchor, j));
			}

			if (path.isSnake()) {
				path = path.prev;
			}
		}
		return changes;
	}
}
