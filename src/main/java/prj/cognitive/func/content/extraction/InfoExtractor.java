package prj.cognitive.func.content.extraction;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.font.PDType1Font;

import prj.cognitive.content.wrapper.DocWrapper;
import prj.cognitive.func.Processor;
import prj.cognitive.func.content.result.Result;
import prj.cognitive.func.content.result.ResultRedirector;
import prj.cognitive.pdf.PdfDocument;
import prj.cognitive.pdf.elements.Node;
import prj.cognitive.pdf.elements.Page;
import prj.cognitive.pdf.elements.Text;
import rst.pdfbox.layout.elements.Document;
import rst.pdfbox.layout.elements.Paragraph;

public class InfoExtractor extends Processor<Result> {

	public static algCode CODE = Processor.algCode.INFO_EXTRACTOR;

	private static InfoExtractor infoExtractor = null;

	private static LinkedList<AbstractExtractor> extractors;

	public static InfoExtractor getInfoExtraction() {
		if (infoExtractor == null) {
			infoExtractor = new InfoExtractor();
			extractors = new LinkedList<AbstractExtractor>();
			extractors.add(new LicenceFeeExtractor());
			extractors.add(new ContractTermsExtractor());
			extractors.add(new LocationExtractor());
			extractors.add(new ServiceChargesExtractor());
			extractors.add(new StartDateExtractor());
		}
		return infoExtractor;
	}

	private InfoExtractor() {
	}

	// TODO Migrate to DocWrapper
	public OutputStream run(OutputStream sos, PdfDocument... document) throws Exception {
		Objects.requireNonNull(sos, "OutputStream must not be null");
		Objects.requireNonNull(document, "document must not be null");
		List<Node> nodes = document[0].getStructuredDocument().getChildren();
		Map<String, String> result = extractInfo(nodes);
		// result.forEach((k, v) -> System.out.println(k + ":" + v));
		saveExtractedREsult(sos, result);
		return sos;
	}

	private Map<String, String> extractInfo(List<Node> nodes) {
		String pageContent = "";
		Map<String, String> result = new HashMap<String, String>();
		LinkedList<AbstractExtractor> exs = new LinkedList<AbstractExtractor>();

		List<Node> pages = nodes.stream().filter(n -> n instanceof Page).collect(Collectors.toList());
		exs.addAll(extractors);

		// int c = 1;
		for (Node p : pages) {
			pageContent = "";
			List<Node> texts = p.getChildren();
			for (Node t_n : texts) {
				Text t = (Text) t_n;
				pageContent += " " + t.getValue();
			}

			pageContent += ".";

			// System.out.println(c++ + "\nPage Content: " + pageContent);
			for (int i = 0; i < exs.size(); i++) {
				AbstractExtractor ex = exs.get(i);
				String str = ex.match(pageContent);
				if (str != null) {
					exs.remove(ex);
					i--;
					result.put(ex.getExtractorName(), str);
				}
			}
			exs.stream().forEach(ex -> result.put(ex.getExtractorName(), ""));
		}
		return result;
	}

	@Override
	public void run(ResultRedirector<Result> redirector, DocWrapper... document)
			throws NullPointerException, IllegalArgumentException, IOException {
	}

	private void saveExtractedREsult(OutputStream sos, Map<String, String> content) throws IOException {
		Map<String, String> treeMap = new TreeMap<>(content);
		Document document = new Document(40, 60, 40, 60);
		treeMap.forEach((k, v) -> {
			try {
				String text = String.format("%s:\n\n%s\n\n\n", k, v);
				Paragraph paragraph = new Paragraph();
				paragraph.addText(text, 8, PDType1Font.HELVETICA);
				paragraph.setMaxWidth(500);
				document.add(paragraph);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		document.save(sos);
	}
}
