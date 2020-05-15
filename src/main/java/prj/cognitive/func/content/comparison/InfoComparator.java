package prj.cognitive.func.content.comparison;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

import prj.cognitive.content.wrapper.DocWrapper;
import prj.cognitive.func.Processor;
import prj.cognitive.func.content.comparison.algorithm.DiffAlgorithmI;
import prj.cognitive.func.content.comparison.algorithm.DiffException;
import prj.cognitive.func.content.comparison.algorithm.MyersDiff;
import prj.cognitive.func.content.comparison.patch.DiffResult;
import prj.cognitive.func.content.elements.Document;
import prj.cognitive.func.content.elements.Node;
import prj.cognitive.func.content.result.ResultRedirector;
import prj.cognitive.pdf.PdfDocument;

public class InfoComparator extends Processor<DiffResult<Node>> {

	public static algCode CODE = Processor.algCode.INFO_COMPARER;

	private InfoComparator() {
	}

	private static InfoComparator infoComparator = null;

	public static InfoComparator getInfoComparator() {
		if (infoComparator == null) {
			infoComparator = new InfoComparator();
		}
		return infoComparator;
	}

	// TODO remove after migrate to DocWrapper
	public OutputStream run(OutputStream outputStream, PdfDocument... document)
			throws NullPointerException, IllegalArgumentException, IOException {
		return outputStream;
	}

	@Override
	public void run(ResultRedirector<DiffResult<Node>> redirector, DocWrapper... document)
			throws NullPointerException, IllegalArgumentException, IOException {
		Objects.requireNonNull(document, "document must not be null");
		if (document.length < 2) {
			throw new IllegalArgumentException("Need two files for compare!!");
		}

		redirector.saveResult(compare(document[0].extractContent(), document[1].extractContent()));
	}

	private DiffResult<Node> compare(Document document1, Document document2) {
		try {
			return diff(document1.getAllWords(), document2.getAllWords(), new MyersDiff<>());
		} catch (DiffException e) {
			e.printStackTrace();
		}
		return null;
	}

	private <T> DiffResult<T> diff(List<T> original, List<T> revised, DiffAlgorithmI<T> algorithm)
			throws DiffException {
		Objects.requireNonNull(original, "original must not be null");
		Objects.requireNonNull(revised, "revised must not be null");
		Objects.requireNonNull(algorithm, "algorithm must not be null");

		return DiffResult.generate(original, revised, algorithm.computeDiff(original, revised));
	}

}
