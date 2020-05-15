package prj.cognitive.func.ocr.utils;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class PunctuationLookup {
	// kind of punctuation that can stop grouping
	private static HashSet<String> stop = new HashSet<String>(Arrays.asList(":", ".", "。"));
	// special type
	private static HashSet<String> special = new HashSet<String>(Arrays.asList(",", ":", ".", "/", "／"));
	private static HashSet<String> split = new HashSet<String>(Arrays.asList("、", ";", "!", "?", "。", "！", "？"));
	private static HashSet<String> connection = new HashSet<String>(Arrays.asList("-", "'", "・", "@"));
	private static HashSet<String> leftBracket = new HashSet<String>(Arrays.asList("(", "⦅", "{", "[", "<", "（", "「",
			"『", "［", "【", "＜", "｟", "〚", "｛", "《", "⟪", "【", "〖", "〈"));
	private static HashSet<String> rightBracket = new HashSet<String>(Arrays.asList(")", "⦆", "}", "]", ">", "）", "」",
			"』", "］", "】", "＞", "｠", "〛", "｝", "》", "⟫", "】", "〗", "〉"));
	private static String verticalBar = "|";
	private static HashMap<String, String> BracketsDictionary = new HashMap<String, String>(
			Map.ofEntries(new AbstractMap.SimpleEntry<String, String>("(", ")"),
					new AbstractMap.SimpleEntry<String, String>("⦅", "⦆"),
					new AbstractMap.SimpleEntry<String, String>("{", "}"),
					new AbstractMap.SimpleEntry<String, String>("[", "]"),
					new AbstractMap.SimpleEntry<String, String>("<", ">"),
					new AbstractMap.SimpleEntry<String, String>("（", "）"),
					new AbstractMap.SimpleEntry<String, String>("「", "」"),
					new AbstractMap.SimpleEntry<String, String>("『", "』"),
					new AbstractMap.SimpleEntry<String, String>("［", "］"),
					new AbstractMap.SimpleEntry<String, String>("【", "】"),
					new AbstractMap.SimpleEntry<String, String>("＜", "＞"),
					new AbstractMap.SimpleEntry<String, String>("｟", "｠"),
					new AbstractMap.SimpleEntry<String, String>("〚", "〛"),
					new AbstractMap.SimpleEntry<String, String>("《", "》"),
					new AbstractMap.SimpleEntry<String, String>("⟪", "⟫"),
					new AbstractMap.SimpleEntry<String, String>("〖", "〗"),
					new AbstractMap.SimpleEntry<String, String>("〈", "〉")));

	public PunctuationLookup() {
	}

	public static boolean IsVerticalBat(String text) {
		return text.equals(verticalBar);
	}

	public static boolean IsPunctuations(String text) {
		return IsSpecial(text) || IsJoin(text) || IsSplit(text) || IsLeftBracket(text) || IsRightBracket(text);
	}

	public static boolean IsStop(String text) {
		return stop.contains(text);
	}

	public static boolean IsSpecial(String text) {
		return special.contains(text);
	}

	public static boolean IsJoin(String text) {
		return connection.contains(text);
	}

	public static boolean IsSplit(String text) {
		return split.contains(text);
	}

	public static boolean IsLeftBracket(String text) {
		return leftBracket.contains(text);
	}

	public static boolean IsRightBracket(String text) {
		return rightBracket.contains(text);
	}

	public static boolean IsPairedBracket(String key, String expected) {
		String value = null;
		value = BracketsDictionary.get(key);
		return value != null ? expected.equalsIgnoreCase(value) : false;
	}
}
