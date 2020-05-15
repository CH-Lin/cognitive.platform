package prj.cognitive.func.content.extraction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

public class StartDateExtractor extends AbstractExtractor {

	public StartDateExtractor() {
		keywords = new String[] { "begin on", "commencement date:", "commencement date", "commencement", "commence on",
				"commence", "period" };
		// debugLog = true; // TODO, remove later
	}

	public String getExtractorName() {
		return "Start Date";
	}

	@Override
	protected String match(String content) {
		log(debugLog, "StartDateExtractor");
		String result = null;
		for (String keyword : keywords) {
			String temp = new String(content);
			int idx = -1;
			while ((idx = temp.toLowerCase().indexOf(keyword)) != -1) {
				temp = temp.substring(keyword.length() + idx);
				log(debugLog, "Temp: " + temp);

				StringTokenizer sz = new StringTokenizer(temp, " ");
				int count = 0;
				String start = "";
				while (sz.hasMoreTokens()) {
					start += sz.nextToken();
					if (++count == 3) {
						break;
					}
					start += "/";
				}
				try {
					start = start.toLowerCase().replaceAll("january", "01").replaceAll("february", "02")
							.replaceAll("march", "03").replaceAll("april", "04").replaceAll("may", "05")
							.replaceAll("june", "06").replaceAll("july", "07").replaceAll("august", "08")
							.replaceAll("september", "09").replaceAll("october", "10").replaceAll("november", "11")
							.replaceAll("december", "12");

					log(debugLog, start);
					SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
					Date date = formatter.parse(start);
					result = formatter.format(date);
					return result;
				} catch (ParseException e) {
					// ignore cannot be converted
				}

			}
		}
		return null;
	}

}
