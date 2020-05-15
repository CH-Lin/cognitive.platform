package prj.cognitive.func.content.comparison.algorithm;

import java.util.Arrays;
import java.util.List;

public interface DiffAlgorithmI<T> {

	List<Change> computeDiff(List<T> source, List<T> target) throws DiffException;

	default List<Change> computeDiff(T[] source, T[] target) throws DiffException {
		return computeDiff(Arrays.asList(source), Arrays.asList(target));
	}
}
