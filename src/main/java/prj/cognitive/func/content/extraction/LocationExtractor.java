package prj.cognitive.func.content.extraction;

import java.util.StringTokenizer;

public class LocationExtractor extends AbstractExtractor {

	private static String countryNames[] = { "Singapore", "China", "Japan", "Taiwan", "India" }; // TODO add more later
																									// or use API to get

	private static String specialCase[] = { "(the licensed area)" }; // TODO add more later

	public LocationExtractor() {
		keywords = new String[] { "premises at", "location", "office at", "business at", "registered address" };
		// debugLog = true; // TODO, remove later
	}

	public String getExtractorName() {
		return "Location";
	}

	@Override
	protected String match(String content) {
		log(debugLog, "LocationExtractor");
		String result = null;
		for (String keyword : keywords) {
			String temp = new String(content);
			int idx = -1;
			while ((idx = temp.toLowerCase().indexOf(keyword)) != -1) {
				temp = temp.substring(keyword.length() + idx);
				log(debugLog, "Temp: " + temp);

				result = "";
				StringTokenizer sz = new StringTokenizer(temp, " ");
				while (sz.hasMoreTokens()) {
					String next = sz.nextToken();
					result += next;
					if (matchContryName(next) || matchSpecialCase(result)) {
						next = sz.nextToken();
						try {
							Integer.parseInt(next);
							result += " " + next;
						} catch (NumberFormatException nfe) {
						}
						return result;
					}
					result += " ";
				}

			}
		}
		return null;
	}

	private boolean matchContryName(String input) {
		if (input == null) {
			return false;
		}
		for (String name : countryNames) {
			if (input.equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	private boolean matchSpecialCase(String input) {
		if (input == null) {
			return false;
		}
		for (String name : specialCase) {
			if (input.toLowerCase().indexOf(name) != -1) {
				return true;
			}
		}
		return false;
	}

}
