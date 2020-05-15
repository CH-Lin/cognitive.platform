package prj.cognitive.func.ocr.utils;

import java.awt.Rectangle;

public class GroupedRegion {
	public String text;
	public int confidence;
	public Rectangle bounds;

	public GroupedRegion(String text, int confidence, Rectangle bounds) {
		this.text = text;
		this.confidence = confidence;
		this.bounds = bounds;
	}
}
