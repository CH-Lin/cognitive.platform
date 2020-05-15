package prj.cognitive.func.ocr.utils;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;

class Tuple<X, Y> {
	public final X item1;
	public final Y item2;

	public Tuple(X item1, Y item2) {
		this.item1 = item1;
		this.item2 = item2;
	}
}

public class MinimumUnitGrouping {
	private static MinimumUnitGrouping instance = null;

	public static MinimumUnitGrouping getInstance(double confidenceScoreThreshold) {
		if (instance == null) {
			instance = new MinimumUnitGrouping(confidenceScoreThreshold);
		} else {
			instance.confidenceScoreThreshold = confidenceScoreThreshold;
		}
		return instance;
	}

	public double confidenceScoreThreshold = 0.8;

	private MinimumUnitGrouping(double ConfidenceScoreThreshold) {
		this.confidenceScoreThreshold = ConfidenceScoreThreshold;
	}

	public GroupedResult Group(List<MinimumUnit> minimumUnitList, Mat lines, DistanceFuncTpye Type) {
		if (minimumUnitList == null) {
			return null;
		}

		LineSearchHelper lineSearchHelper = new LineSearchHelper(lines);

		List<GroupedRegion> combined = new ArrayList<GroupedRegion>();
		List<GroupedRegion> lowConfidence = new ArrayList<GroupedRegion>();
		List<GroupedRegion> allRegions = new ArrayList<GroupedRegion>();
		List<MinimumUnit> units = new ArrayList<MinimumUnit>();
		int lastXDistance = 0;

		MinimumUnit MinimumUnitWithLeftBracket = null;

		// TO-DO, sort minimum units based on y and x here

		// TO-DO, use table line to enhance grouping
		for (int index = 0; index < minimumUnitList.size(); index++) {
			MinimumUnit current = minimumUnitList.get(index);

			if (units.size() != 0) {
				// previous minimum unit
				MinimumUnit previous = units.get(units.size() - 1);
				// MinimumUnit previous = units.LastOrDefault();
				MinimumUnit next = null;
				int xBackDistance = 0;

				if (previous.IsStop()) {
					allRegions.add(CombineUnits(units));
					units.clear();
					lastXDistance = 0;
				} else {
					if (current != (minimumUnitList.get(minimumUnitList.size() - 1))) {
						// next minimum unit
						next = minimumUnitList.get(index + 1);
						xBackDistance = next.GetRealUpLeft().x - current.GetRealUpRight().x;
					}

					// check syntax
					if (current.IsPunctuations()) {
						current.type = UnitType.Punctuation;
						boolean oneLine = IsSameLine(previous, current);
						if (oneLine) {
							if (!current.IsRightBracket()) {
								if (!ExistLineBetweenUnits(lineSearchHelper, previous, current)) {
									if (current.IsStop()) {
										// TO-DO, review
										current.upRight = previous.upRight;
										current.downRight = previous.downRight;
										previous.isBreak = true;
										current.isBreak = true;
										previous.Append(current);
										allRegions.add(CombineUnits(units));
										units.clear();
										lastXDistance = 0;
									} else if (current.IsJoin()) {
										previous.Append(current);
										previous.type = UnitType.CharWithJoin;
									} else if (current.IsSplit()) {
										previous.Append(current);
										previous.isBreak = true;
									} else if (current.IsLeftBracket() && IsSameLine(next, current)
											&& !ExistLineBetweenUnits(lineSearchHelper, current, next)) {
										next.Prepend(current);
										MinimumUnitWithLeftBracket = next;
									} else if (IsSameLine(next, current)
											&& !ExistLineBetweenUnits(lineSearchHelper, current, next)) {
										next.Prepend(current);
									}
								} else if (IsSameLine(next, current)
										&& !ExistLineBetweenUnits(lineSearchHelper, current, next)) {
									if (current.IsLeftBracket()) {
										MinimumUnitWithLeftBracket = next;
									}
									next.Prepend(current);
								}
							} else if (!ExistLineBetweenUnits(lineSearchHelper, previous, current)) {
								if (MinimumUnitWithLeftBracket != null) {
									String leftText = MinimumUnitWithLeftBracket.GetPrependText();
									if (!PunctuationLookup.IsPairedBracket(leftText, current.text)) {
										// discard prepend because they are not pair
										MinimumUnitWithLeftBracket.DiscardPrepend();
									} else {
										previous.Append(current);
									}
									MinimumUnitWithLeftBracket = null;
								}
							}
						} else if (IsSameLine(next, current)
								&& !ExistLineBetweenUnits(lineSearchHelper, current, next)) {
							if (current.IsLeftBracket()) {
								MinimumUnitWithLeftBracket = next;
							}
							next.Prepend(current);
						}
						continue;
					}

					// check distance
					int xFrontDistance = current.GetRealUpLeft().x - previous.GetRealUpRight().x;
					double angle = GetAngle(previous.GetRealUpRight(), current.GetRealUpLeft());
					boolean isOneWord = CanCombine(xFrontDistance, xBackDistance, lastXDistance, angle,
							DistanceFunctions.GetDistanceFunc(Type).calculate(minimumUnitList, index));
					lastXDistance = xFrontDistance;

					// additional checking by table line support
					if (isOneWord && lines != null) {
						isOneWord = !ExistLineBetweenUnits(lineSearchHelper, previous, current);
					}

					if (!isOneWord) {
						// create a word and add it to word regions.
						allRegions.add(CombineUnits(units));
						units.clear();
						lastXDistance = 0;

						// line break so separate
						if (current.isBreak) {
							MoveToResultLists(combined, lowConfidence, allRegions, confidenceScoreThreshold * 100);
							allRegions.clear();
						}
					}
				}
			}
			units.add(current);
		}

		allRegions.add(CombineUnits(units));
		MoveToResultLists(combined, lowConfidence, allRegions, confidenceScoreThreshold * 100);

		return new GroupedResult(combined, lowConfidence);
	}

	private boolean ExistLineBetweenUnits(LineSearchHelper lineSearchHelper, MinimumUnit unit1, MinimumUnit unit2) {
		MinimumUnit left, right;
		if (unit1.GetRealUpRight().x < unit2.GetRealUpRight().x) {
			left = unit1;
			right = unit2;
		} else {
			left = unit2;
			right = unit1;
		}
		int width = Math.abs(right.GetRealUpRight().x - left.GetRealUpLeft().x);
		int height = Math.abs(Math.max(right.GetRealDownLeft().y, left.GetRealDownLeft().y)
				- Math.min(right.GetRealUpLeft().y, left.GetRealUpLeft().y));
		Rectangle rect = new Rectangle(left.GetRealUpLeft().x, left.GetRealUpLeft().y, width, height);

		if (lineSearchHelper.existLineOnTheRegion(rect)) {
			return true;
		}
		return false;
	}

	private boolean IsSameLine(MinimumUnit source, MinimumUnit target) {
		if (source == null || target == null) {
			return false;
		}
		Tuple<Integer, Integer> tuple = GetMinMaxY(source);
		Tuple<Integer, Integer> tuple2 = GetMinMaxY(target);
		Point centerPoint1 = GetCenterPoint(source);
		Point centerPoint2 = GetCenterPoint(target);
		int centerYDifference = Math.abs(centerPoint1.y - centerPoint2.y);
		int minYDifference = Math.abs(tuple.item1 - tuple2.item1);
		int maxYDifference = Math.abs(tuple.item2 - tuple2.item2);
		if (centerYDifference <= 5 && minYDifference <= 5 && maxYDifference <= 5)
			return true;
		return false;
	}

	private Tuple<Integer, Integer> GetMinMaxY(MinimumUnit unit) {
		// crash it in debug build to make sure all the caller never pass null and find
		// the caller pass null into this function
		// System.Diagnostics.Debug.Assert(unit != null);
		int lowY = Math.min(unit.GetRealUpRight().y, unit.GetRealUpLeft().y);
		int highY = Math.max(unit.GetRealDownRight().y, unit.GetRealDownLeft().y);
		return new Tuple<Integer, Integer>(lowY, highY);
	}

	private Point GetCenterPoint(MinimumUnit unit) {
		// crash it in debug build to make sure all the caller never pass null and find
		// the caller pass null into this function
		// System.Diagnostics.Debug.Assert(unit != null);
		int centerX = unit.GetRealUpLeft().x + unit.GetRealUpRight().x + unit.GetRealDownRight().x
				+ unit.GetRealDownLeft().x;
		int centerY = unit.GetRealUpLeft().y + unit.GetRealUpRight().y + unit.GetRealDownRight().y
				+ unit.GetRealDownLeft().y;
		return new Point(centerX / 4, centerY / 4);
	}

	private double GetAngle(Point p1, Point p2) {
		int xDiff = p2.x - p1.x;
		int yDiff = p2.y - p1.y;
		double angle = Math.atan2(yDiff, xDiff) * (180 / Math.PI);
		return angle >= 180 ? angle - 180 : angle;
	}

	private boolean CanCombine(int frontDistance, int backDistance, int lastXDistance, double angle, int threshold) {
		if (angle < 30.0) {
			frontDistance = Math.abs(frontDistance);
			backDistance = Math.abs(backDistance);
			if (backDistance != 0) {
				if (Math.abs(frontDistance - backDistance) < threshold
						|| Math.abs(frontDistance - lastXDistance) < threshold) {
					return true;
				}
			} else {
				if (Math.abs(frontDistance - lastXDistance) < threshold) {
					return true;
				}
			}
		}
		return false;
	}

	// TO-DO, quickest and simplest bounding box calculation, no need to check all
	// vertex because it is rectangle
	private GroupedRegion CombineUnits(List<MinimumUnit> symbols) {
		// text
		String text = "";
		for (MinimumUnit s : symbols) {
			text += s.getText();
		}

		// TO-DO, speed up bounding box calculation
		int minX = 0, maxX = 0, minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
		if (symbols.size() > 0) {
			minX = symbols.get(0).GetRealUpLeft().x;
			maxX = symbols.get(symbols.size() - 1).GetRealUpRight().x;
			for (MinimumUnit symbol : symbols) {
				if (symbol.GetRealUpLeft().y < minY) {
					minY = symbol.GetRealUpLeft().y;
				}
				if (symbol.GetRealDownLeft().y > maxY) {
					maxY = symbol.GetRealDownLeft().y;
				}
			}
		}
		int width = maxX - minX;
		int height = maxY - minY;
		if (text != null && !text.equalsIgnoreCase("")) // seems not necessary, need to be review
		{
			width = width <= 0 ? 1 : width;
			height = height <= 0 ? 1 : height;
		}
		Rectangle bounds = new Rectangle(minX, minY, maxX - minX, maxY - minY);

		// confidence
		double average = (symbols.stream().map(r -> r.confidence).reduce((a, b) -> a + b)).orElse((float) 0)
				/ (double) symbols.size();
		int confidence = (int) Math.round(average * 100);
		return new GroupedRegion(text, confidence, bounds);
	}

	private void MoveToResultLists(List<GroupedRegion> resultList, List<GroupedRegion> lowConfidenceList,
			List<GroupedRegion> sourceList, double confidenceScoreThreshold) {
		for (GroupedRegion region : sourceList) {
			if (region.confidence <= confidenceScoreThreshold) {
				lowConfidenceList.add(region);
			} else {
				resultList.add(region);
			}
		}
	}
}
