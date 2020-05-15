package prj.cognitive.func.ocr.utils;

import java.awt.Rectangle;

import org.opencv.core.Mat;

public class LineSearchHelper {
	private Mat lines = null;

	public LineSearchHelper(Mat lines) {
		this.lines = lines;
		// TO-DO, add necessary catch for lines
	}

	public boolean existLineOnTheRegion(Rectangle rectangle) {
//        // TO-DO, GetLineOnTheWord function cost O(n) for each symbol. It will be totally O(m x n) for all processing. Too slow, improve it.
//        boolean foundLine = LineDetection.GetLineOnTheWord(rectangle, Lines);
//        if (foundLine.Length > 0)
//        {
//            return true;
//        }
		return false;
	}
}
