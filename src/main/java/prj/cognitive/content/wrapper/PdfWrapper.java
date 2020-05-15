package prj.cognitive.content.wrapper;

import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import prj.cognitive.func.content.elements.Document;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PdfWrapper implements DocWrapper, Closeable {
	public final String PAGE_END_VALUE = "**EOP**";
	private PDDocument pddDocument;
	private Document docContent;

	public PdfWrapper() {
		docContent = new Document();
	}

	public PdfWrapper(InputStream stream) throws IOException {
		assert (stream != null);
		ByteSource documentData = ByteSource.wrap(ByteStreams.toByteArray(stream));
		PDFParser parser = new PDFParser(new RandomAccessBuffer(documentData.openStream()));
		parser.parse();
		pddDocument = parser.getPDDocument();
		docContent = new Document();
	}

	public PdfWrapper(PDDocument pddDocument) throws IOException {
		assert (pddDocument != null);
		this.pddDocument = pddDocument;
		docContent = new Document();
	}

	public Document extractContent() throws IOException {
		assert (pddDocument != null);
		PDFTextStripper stripper = new PDDWrapperTextStripper();
		stripper.setPageEnd(PAGE_END_VALUE);
		stripper.getText(pddDocument);
		return docContent;
	}

	public PDDocument getPDDocument() {
		return pddDocument;
	}

	public class PDDWrapperTextStripper extends PDFTextStripper {

		public PDDWrapperTextStripper() throws IOException {
			super();
		}

		protected void startPage(PDPage page) throws IOException {
			docContent.addNewPage(super.getCurrentPageNo(), 0, 0, page.getMediaBox().getHeight(),
					page.getMediaBox().getHeight());
			super.startPage(page);
		}

		protected void endPage(PDPage page) throws IOException {
			docContent.endOfPage();
			super.endPage(page);
		}

		protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
			docContent.addContent(text, textPositions);
			super.writeString(text, textPositions);
		}
	}

	public void close() throws IOException {
		if (pddDocument != null) {
			pddDocument.close();
		}
	}

}
