package prj.cognitive.func.ocr.utils;

import java.awt.Point;

enum UnitType {
	CJK, Latin, Number, Punctuation, CharWithJoin, TableLine
}

public class MinimumUnit {

	public Point upLeft;
	public Point upRight;
	public Point downRight;
	public Point downLeft;
	public String text;
	public boolean isBreak;
	public float confidence;
	public UnitType type;

	private MinimumUnit PrependedPunctuation = null;

	private MinimumUnit AppendedPunctuation = null;

	public String getText() {
		String PrependedText = (PrependedPunctuation != null) ? PrependedPunctuation.text : "";
		String ApppendedText = (AppendedPunctuation != null) ? AppendedPunctuation.text : "";
		String LatinText = (type == UnitType.Latin) ? " " : "";
		return PrependedText + text + ApppendedText + LatinText;
	}

	public Point GetRealUpLeft() {
		return (PrependedPunctuation != null) ? PrependedPunctuation.upLeft : upLeft;
	}

	public Point GetRealUpRight() {
		return (AppendedPunctuation != null) ? AppendedPunctuation.upRight : upRight;
	}

	public Point GetRealDownRight() {
		return (AppendedPunctuation != null) ? AppendedPunctuation.downRight : downRight;
	}

	public Point GetRealDownLeft() {
		return (PrependedPunctuation != null) ? PrependedPunctuation.downLeft : downLeft;
	}

	public int GetWidth() {
		return GetRealUpRight().x - GetRealUpLeft().x;
	}

	public int GetHeight() {
		return GetRealUpRight().y - GetRealDownRight().y;
	}

	public boolean IsPunctuations() {
		return PunctuationLookup.IsPunctuations(text);
	}

	public boolean IsLeftBracket() {
		return PunctuationLookup.IsLeftBracket(text);
	}

	public boolean IsRightBracket() {
		return PunctuationLookup.IsRightBracket(text);
	}

	public boolean IsSpecial() {
		return PunctuationLookup.IsSpecial(text);
	}

	public boolean IsStop() {
		return PunctuationLookup.IsStop(text);
	}

	public boolean IsJoin() {
		return PunctuationLookup.IsJoin(text);
	}

	public boolean IsSplit() {
		return PunctuationLookup.IsSplit(text);
	}

	public void Prepend(MinimumUnit unit) {
		PrependedPunctuation = unit;
	}

	public void Prepend(String text, Point UpLeft, Point DownLeft) {
		PrependedPunctuation = new MinimumUnit();
		PrependedPunctuation.text = text;
		PrependedPunctuation.upLeft = UpLeft;
		PrependedPunctuation.downLeft = DownLeft;
	}

	public void Append(MinimumUnit unit) {
		AppendedPunctuation = unit;
	}

	public void Append(String text, Point UpRight, Point DownRight) {
		AppendedPunctuation = new MinimumUnit();
		AppendedPunctuation.text = text;
		AppendedPunctuation.upRight = UpRight;
		AppendedPunctuation.downRight = DownRight;
	}

	public void DiscardPrepend() {
		this.PrependedPunctuation = null;
	}

	public void DiscardAppend() {
		this.AppendedPunctuation = null;
	}

	public String GetPrependText() {
		return (this.PrependedPunctuation != null) ? this.PrependedPunctuation.text : "";
	}
}
