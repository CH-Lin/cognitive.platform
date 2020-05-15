package prj.cognitive.func.content.extraction;

public abstract class AbstractExtractor {

	public String keywords[];

	public abstract String getExtractorName();

	protected abstract String match(String content);

	// TODO, remove later
	protected boolean debugLog = false;

	// TODO, remove later
	protected void log(boolean enable, String str) {
		if (debugLog) {
			System.out.println(str);
		}
	}
}
