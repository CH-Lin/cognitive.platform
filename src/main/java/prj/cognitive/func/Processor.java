package prj.cognitive.func;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import prj.cognitive.content.wrapper.DocWrapper;
import prj.cognitive.func.content.comparison.InfoComparator;
import prj.cognitive.func.content.extraction.InfoExtractor;
import prj.cognitive.func.content.result.Result;
import prj.cognitive.func.content.result.ResultRedirector;
import prj.cognitive.pdf.PdfDocument;

public abstract class Processor<T extends Result> {

	public enum algCode {
		INFO_EXTRACTOR, INFO_COMPARER
	};

	private static Map<algCode, Processor<?>> algs = new HashMap<algCode, Processor<?>>();

	public static Processor<? extends Result> get(algCode code) {
		if (algs.get(code) == null) {
			algs.put(InfoExtractor.CODE, InfoExtractor.getInfoExtraction());
			algs.put(InfoComparator.CODE, InfoComparator.getInfoComparator());
		}
		return algs.get(code);
	}

	// TODO Migrate to DocWrapper
	public abstract OutputStream run(OutputStream sos, PdfDocument... document) throws Exception;

	public abstract void run(ResultRedirector<T> redirector, DocWrapper... document) throws Exception;
}
