package prj.cognitive.func.content.result;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;

import prj.cognitive.content.wrapper.PdfWrapper;
import prj.cognitive.func.content.comparison.patch.AbstractDelta;
import prj.cognitive.func.content.comparison.patch.DiffResult;
import prj.cognitive.func.content.elements.Node;
import prj.cognitive.func.content.elements.Page;
import prj.cognitive.func.content.elements.Word;

public class PdfDiffMaskRedirector extends ResultRedirector<DiffResult<Node>> {

	private OutputStream sos;
	private PdfWrapper pdf;

	public PdfDiffMaskRedirector(OutputStream sos, PdfWrapper pdf) {
		this.sos = sos;
		this.pdf = pdf;
	}

	public String getResponseContentType() {
		return "application/pdf";
	}

	@Override
	public boolean saveResult(DiffResult<Node> result) {
		try {
			for (AbstractDelta<Node> delta : result.getDeltas()) {
				for (Node node : delta.getTarget().getLines()) {
					if (node instanceof Word) {
						Word word = (Word) node;
						Node n = word.getParent();
						int pageNumber = 0;
						if (n instanceof Page) {
							Page page = (Page) n;
							pageNumber = page.getPageNumber();
						}
						// System.out.println(delta);
						mask(pageNumber, Color.yellow, 0.4, word.getArea().getX(), word.getArea().getY(),
								word.getArea().getWidth(), word.getArea().getHeight());
					}
				}
			}
			pdf.getPDDocument().save(sos);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// same with highlight in PdfDocument class
	public void mask(int page, Color color, double opacity, double x, double y, double width, double height)
			throws IOException {
		PDDocument document = pdf.getPDDocument();
		PDPageContentStream contentStream = new PDPageContentStream(document, document.getPage(page - 1),
				PDPageContentStream.AppendMode.APPEND, true);
		PDExtendedGraphicsState graphics = new PDExtendedGraphicsState();
		graphics.setNonStrokingAlphaConstant((float) opacity);
		contentStream.setGraphicsStateParameters(graphics);
		contentStream.setNonStrokingColor(color);
		contentStream.addRect((float) x, (float) (document.getPage(page - 1).getBleedBox().getHeight() - height - y),
				(float) width, (float) height);
		contentStream.fill();
		contentStream.close();
	}

}
