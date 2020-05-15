package prj.cognitive.func.ocr.utils;

import java.util.List;

public class GroupedResult {
	public List<GroupedRegion> groupedRegions;
	public List<GroupedRegion> lowConfidenceRegions;

	public GroupedResult(List<GroupedRegion> groupedRegions, List<GroupedRegion> lowConfidenceRegions) {
		this.groupedRegions = groupedRegions;
		this.lowConfidenceRegions = lowConfidenceRegions;
	}
}
