package prj.cognitive.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import javax.imageio.ImageIO;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import prj.cognitive.pdf.elements.attrs.PositionalArea;

public class ImageExtraction {

	public static Mat BufferedImage2Mat(BufferedImage image) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ImageIO.write(image, "jpg", byteArrayOutputStream);
		byteArrayOutputStream.flush();
		return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()),
				Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
	}

	public static BufferedImage Mat2BufferedImage(Mat matrix) throws IOException {
		MatOfByte mob = new MatOfByte();
		Imgcodecs.imencode(".jpg", matrix, mob);
		return ImageIO.read(new ByteArrayInputStream(mob.toArray()));
	}

	public static PositionalArea enlargeToBorders(PositionalArea originalRegion, BufferedImage pageImage) {
		try {
			long start = System.currentTimeMillis();
			nu.pattern.OpenCV.loadShared();

			double originalX = originalRegion.getX();
			double originalY = originalRegion.getY();

			Mat m = BufferedImage2Mat(pageImage);
			Mat gray = new Mat(m.size(), CvType.CV_8UC1);
			Mat cannyEdges = new Mat();
			Mat lines = new Mat();

			Imgproc.cvtColor(m, gray, Imgproc.COLOR_RGB2GRAY);
			Imgproc.Canny(gray, cannyEdges, 150, 100);
			Imgproc.HoughLinesP(cannyEdges, lines, 1, Math.PI / 180, 50, 200, 10);

			ArrayList<Line> up = new ArrayList<Line>();
			ArrayList<Line> down = new ArrayList<Line>();
			ArrayList<Line> left = new ArrayList<Line>();
			ArrayList<Line> right = new ArrayList<Line>();

			for (int i = 0; i < lines.rows(); i++) {
				double[] points = lines.get(i, 0);

				Line line = new Line(points);
				double distance = line.distanceToPoint(originalX, originalY); // Diagonal will be filtered after this
				if (distance != Double.MAX_VALUE) { // keep lines only with reasonable distance
					switch (LineClassifier.identify(line, originalX, originalY)) {
					case UP:
						up.add(line);
						break;
					case DOWN:
						down.add(line);
						break;
					case LEFT:
						left.add(line);
						break;
					case RIGHT:
						right.add(line);
						break;
					default:
					}
				}
			}

			sortByDistance(up, originalX, originalY);
			sortByDistance(down, originalX, originalY);
			sortByDistance(left, originalX, originalY);
			sortByDistance(right, originalX, originalY);

			Mat houghLines = new Mat();
			houghLines.create(cannyEdges.rows(), cannyEdges.cols(), Imgproc.COLOR_RGB2GRAY);

			Box box = Box.findBox(up, down, left, right);
			if (box.isVaild()) {
				originalRegion.setX(box.getX());
				originalRegion.setY(box.getY());
				originalRegion.setWidth(box.getWidth());
				originalRegion.setHeight(box.getHeight());
			}

			long finish = System.currentTimeMillis();
			System.out.println("Time for imsge extraction: " + (finish - start) + " milliseconds");

		} catch (IOException e) {
			e.printStackTrace();
		}
		return originalRegion;
	}

	private static void sortByDistance(ArrayList<Line> lineList, double x, double y) {
		Collections.sort(lineList, (a, b) -> {
			return (int) (a.distanceToPoint(x, y) - b.distanceToPoint(x, y));
		});
	}

	private static class Box {
		private int count[];
		private Line upBottom[];
		private Line leftRight[];
		private double x, y;
		private double width, height;

		public Box() {
			count = new int[2];
			upBottom = new Line[2];
			leftRight = new Line[2];

			count[0] = count[1] = 0;
			x = Double.MAX_VALUE;
			y = Double.MAX_VALUE;
			width = Double.MAX_VALUE;
			height = Double.MAX_VALUE;
		}

		public void addLine(Line line) {
			if (line.isDiagonal())
				throw new IllegalArgumentException("Diagonal line is invalid");
			if (line.isVertical()) {
				if (line.getX1() < x) {
					x = line.getX1();
				}
				leftRight[count[0]] = line;
				count[0]++;
				if (count[0] == 2) {
					width = Math.abs(leftRight[0].getX1() - leftRight[1].getX1());
				}
			} else {
				if (line.getY1() < y) {
					y = line.getY1();
				}
				upBottom[count[1]] = line;
				count[1]++;
				if (count[1] == 2) {
					height = Math.abs(upBottom[0].getY1() - upBottom[1].getY1());
				}
			}
		}

		public static Box findBox(ArrayList<Line> up, ArrayList<Line> down, ArrayList<Line> left,
				ArrayList<Line> right) {

			Box box = new Box();
			boolean upStatus = false, downStatus = false, leftStatus = false, rightStatus = false;

			try {
				Line upLine = up.remove(0);
				Line downLine = down.remove(0);
				Line leftLine = left.remove(0);
				Line rightLine = right.remove(0);

				while (!upStatus || !downStatus || !leftStatus || !rightStatus) {
					upStatus = getIntersetCount(upLine, leftLine, rightLine);
					downStatus = getIntersetCount(downLine, leftLine, rightLine);
					leftStatus = getIntersetCount(leftLine, upLine, downLine);
					rightStatus = getIntersetCount(rightLine, upLine, downLine);
					if (!upStatus) {
						upLine = up.remove(0);
					}
					if (!downStatus) {
						downLine = up.remove(0);
					}
					if (!leftStatus) {
						leftLine = up.remove(0);
					}
					if (!rightStatus) {
						rightLine = up.remove(0);
					}
				}
				box.addLine(upLine);
				box.addLine(downLine);
				box.addLine(leftLine);
				box.addLine(rightLine);
			} catch (IndexOutOfBoundsException  e) {
				// Cannot find lines to compose box
				e.printStackTrace();
			}

			return box;
		}

		private static boolean getIntersetCount(Line target, Line line1, Line line2) {
			if (!target.isIntersect(line1) && !target.isIntersect(line2))
				return false;
			return true;
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

		public double getWidth() {
			return width;
		}

		public double getHeight() {
			return height;
		}

		public boolean isVaild() {
			return count[0] == 2 && count[1] == 2;
		}
	}

	private static class LineClassifier {
		enum Position {
			UP, DOWN, LEFT, RIGHT, NaN
		}

		public static Position identify(Line line, double X, double Y) {
			if (line.isHorizontal()) {
				if (line.getY1() > Y) {
					return Position.UP;
				} else {
					return Position.DOWN;
				}
			} else if (line.isVertical()) {
				if (line.getX1() > X) {
					return Position.RIGHT;
				} else {
					return Position.LEFT;
				}
			}
			return Position.NaN;
		}
	}

	private static class Line {
		private static final int LINE_SLOPE_THRESHOLD = Config.get(Images.class, "LINE_SLOPE_THRESHOLD", 10);
		private static final int DISTANCE_THRESHOLD = Config.get(Images.class, "DISTANCE_THRESHOLD", 20);

		enum Type {
			DIAGONAL, VERTICAL, HORIZONTAL
		}

		private double x1, y1, x2, y2; // point of this line (x1, y1) -> (x2, y2)
		private double distance; // distance to point (destinationX, destinationY)
		private double destincationX, destincationY;

		private Type type;

		public Line(double[] points) {
			if (points == null)
				throw new IllegalArgumentException("Need points data for line");
			if (points.length < 4)
				throw new IllegalArgumentException("Number of points are not enough");

			x1 = points[0];
			y1 = points[1];
			x2 = points[2];
			y2 = points[3];

			if ((Math.abs(x1 - x2) < LINE_SLOPE_THRESHOLD) && (Math.abs(y1 - y2) > LINE_SLOPE_THRESHOLD)) {
				type = Type.VERTICAL;
			} else if ((Math.abs(x1 - x2) > LINE_SLOPE_THRESHOLD) && (Math.abs(y1 - y2) < LINE_SLOPE_THRESHOLD)) {
				type = Type.HORIZONTAL;
			} else {
				type = Type.DIAGONAL;
			}

			distance = -Double.MAX_VALUE;
			destincationX = -Double.MAX_VALUE;
			destincationY = -Double.MAX_VALUE;
		}

		public double getX1() {
			return x1;
		}

		public double getY1() {
			return y1;
		}

		public double getX2() {
			return x2;
		}

		public double getY2() {
			return y2;
		}

		public double distanceToPoint(double X, double Y) {
			if (X != destincationX || Y != destincationY) {
				if (isDiagonal()) { // set distance to maximum value to ignore all diagonal lines
					distance = Double.MAX_VALUE;
					return distance;
				}
				if (isVertical() && (y2 - DISTANCE_THRESHOLD < Y) && (Y < y1 + DISTANCE_THRESHOLD)) {
					if (X > x1) {
						distance = X - Math.max(x1, x2);
					} else {
						distance = Math.min(x1, x2) - X;
					}
				} else if (isHorizontal() && (x1 - DISTANCE_THRESHOLD < X) && (X < x2 + DISTANCE_THRESHOLD)) {
					if (Y > y1) {
						distance = Y - Math.max(y1, y2);
					} else {
						distance = Math.min(y1, y2) - Y;
					}
				} else {
					distance = Double.MAX_VALUE;
				}
			}
			return distance;
		}

		public boolean isIntersect(Line line) {
			if ((isHorizontal() == line.isHorizontal()) || (isVertical() == line.isVertical())) {
			} else if (isVertical() == line.isHorizontal()) {
				if ((line.getX1() - DISTANCE_THRESHOLD < Math.max(x1, x2)
						|| line.getX2() + DISTANCE_THRESHOLD > Math.min(x1, x2))
						&& (y2 - DISTANCE_THRESHOLD < Math.max(line.getY1(), line.getY2())
								|| y1 + DISTANCE_THRESHOLD < Math.min(line.getY1(), line.getY2()))) {
					return true;
				}
			} else if (isHorizontal() == line.isVertical()) {
				if ((x1 - DISTANCE_THRESHOLD < Math.max(line.getX1(), line.getX2())
						|| x2 + DISTANCE_THRESHOLD > Math.min(line.getX1(), line.getX2()))
						&& (line.getY2() - DISTANCE_THRESHOLD < Math.max(y1, y2)
								|| line.getY1() + DISTANCE_THRESHOLD < Math.min(y1, y2))) {
					return true;
				}
			}
			return false;
		}

		public boolean isVertical() {
			return type == Type.VERTICAL;
		}

		public boolean isHorizontal() {
			return type == Type.HORIZONTAL;
		}

		public boolean isDiagonal() {
			return type == Type.DIAGONAL;
		}
	}

}
