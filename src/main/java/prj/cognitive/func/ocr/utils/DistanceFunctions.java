package prj.cognitive.func.ocr.utils;

import java.util.HashMap;
import java.util.List;

enum DistanceFuncTpye {
	Fixed, FuzzyInference
}

interface IDistanceFunc {
	int calculate(List<MinimumUnit> minimumUnitList, int idxk);
}

public class DistanceFunctions {

	private static HashMap<DistanceFuncTpye, IDistanceFunc> funcs;

	public DistanceFunctions() {
		funcs = new HashMap<DistanceFuncTpye, IDistanceFunc>();
		funcs.put(FixNumberDistanceFunc.type, new FixNumberDistanceFunc());
		funcs.put(FuzzyInferenceDistanceFunc.type, new FuzzyInferenceDistanceFunc());
	}

	public static IDistanceFunc GetDistanceFunc(DistanceFuncTpye type) {
		return funcs.get(type);
	}
}

class FixNumberDistanceFunc implements IDistanceFunc {
	public static DistanceFuncTpye type = DistanceFuncTpye.Fixed;

	public int calculate(List<MinimumUnit> minimumUnitList, int idxk) {
		return 15;
	}
}

class FuzzyInferenceDistanceFunc implements IDistanceFunc {
	public static DistanceFuncTpye type = DistanceFuncTpye.FuzzyInference;

	public int calculate(List<MinimumUnit> minimumUnitList, int idxk) {
		// Fuzzy membership functions
		return 0;
	}
}