package prj.cognitive.func.content.comparison.algorithm;

import prj.cognitive.func.content.comparison.patch.DeltaType;

public class Change {

	public final DeltaType deltaType;
	public final int startOriginal;
	public final int endOriginal;
	public final int startRevised;
	public final int endRevised;

	public Change(DeltaType deltaType, int startOriginal, int endOriginal, int startRevised, int endRevised) {
		this.deltaType = deltaType;
		this.startOriginal = startOriginal;
		this.endOriginal = endOriginal;
		this.startRevised = startRevised;
		this.endRevised = endRevised;
	}
}
