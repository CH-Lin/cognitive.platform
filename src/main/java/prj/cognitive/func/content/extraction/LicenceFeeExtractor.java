package prj.cognitive.func.content.extraction;

import java.util.StringTokenizer;

public class LicenceFeeExtractor extends AbstractExtractor {

	public LicenceFeeExtractor() {
		keywords = new String[] { "licence fee,", "licence fee" };
		// debugLog = true; // TODO, remove later
	}

	private String unnecessaryText[] = { "for the Stations as follows: " };

	public String getExtractorName() {
		return "Licence Fee";
	}

	@Override
	protected String match(String content) {
		log(debugLog, "LicenceFeeExtractor");
		String result = null;
		for (String keyword : keywords) {
			String temp = new String(content);
			int idx = -1;
			while ((idx = temp.toLowerCase().indexOf(keyword)) != -1) {
				temp = temp.substring(keyword.length() + idx);
				log(debugLog, "Temp: " + temp);

				String temp2 = new String(temp);
				int idx1 = temp2.indexOf(".");
				int idx2 = temp2.indexOf("/");
				temp2 = temp2.substring(0, idx1 > idx2 ? idx1 : idx2);
				if (temp2.indexOf("$") == -1 && temp2.indexOf("SGD") == -1) {
					continue;
				}
				log(debugLog, "Temp2: " + temp2);

				StringTokenizer sz = new StringTokenizer(temp, " ");
				String temp3 = "";
				while (sz.hasMoreTokens()) {
					temp3 += sz.nextToken();
					if (temp3.endsWith(".") || temp3.endsWith("/")) {
						int idx_dolor = -1;
						if (((idx_dolor = temp3.indexOf("$")) != -1 || (idx_dolor = temp3.indexOf("SGD")) != -1)
								&& ((temp3.toLowerCase().indexOf("service charge") == -1)
										|| temp3.toLowerCase().indexOf("service charge") > idx_dolor)) {

							for (String rmText : unnecessaryText) {
								if (temp3.indexOf(rmText) != -1) {
									String temp4 = "";
									temp4 += temp3.substring(0, temp3.indexOf(rmText));
									temp4 += temp3.substring(temp3.indexOf(rmText) + rmText.length());
									temp3 = temp4;
								}
							}

							result = temp3.substring(0, 1).toUpperCase() + temp3.substring(1);
							return result;
						}
						break;
					}
					temp3 += " ";

					log(debugLog, "Temp3: " + temp3);
				}
			}
		}
		return null;
	}

}
