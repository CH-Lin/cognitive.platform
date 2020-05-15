package prj.cognitive.func.content.extraction;

public class ContractTermsExtractor extends AbstractExtractor {

	public ContractTermsExtractor() {
		keywords = new String[] { "licence period", "years" };
		// debugLog = true; // TODO, remove later
	}

	public String getExtractorName() {
		return "Contract Terms";
	}

	boolean enable = false;

	@Override
	protected String match(String content) {
		log(debugLog, "ContractTermsExtractor");
		String result = null;

		return result;
	}

}
