package prj.cognitive.pdf;

import prj.cognitive.pdf.elements.Character;
import prj.cognitive.pdf.elements.Document;
import prj.cognitive.pdf.elements.Page;
import prj.cognitive.pdf.elements.Text;
import prj.cognitive.pdf.elements.attrs.PositionalArea;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StructuredPdfBuilder {
    public final String PAGE_END_VALUE = "!!____THISISAPAGEENDFORIQBOTTOFIND_____!!";
    protected String text;
    protected Document structuredDocument = new Document();

    public StructuredPdfBuilder(PDDocument doc) throws IOException {
        PDFTextStripper stripper = new UnstructuredTextStripper();
        stripper.setPageEnd(PAGE_END_VALUE);
        text = stripper.getText(doc);
    }

    public List<String> getText() {
        return Arrays.asList(text.split(PAGE_END_VALUE));
    }

    public Document getStructuredDocument() {
        return structuredDocument;
    }

    public class UnstructuredTextStripper extends PDFTextStripper {
        protected Page currentPage;

        public UnstructuredTextStripper() throws IOException {
            super();
        }

        protected void startPage(PDPage page) throws IOException {
            currentPage = new Page(structuredDocument);
            currentPage.setPageNumber(super.getCurrentPageNo());

            super.startPage(page);
        }

        protected void endPage(PDPage page) throws IOException {
            Collections.sort(currentPage.getChildren());

            structuredDocument.appendChild(currentPage);

            super.endPage(page);
        }

        protected void writeString(String text, List<TextPosition> characters) throws IOException {
            Text textNode = new Text(currentPage);

            if (currentPage.getShape() == null) {
                currentPage.setShape(new PositionalArea());
                currentPage.getShape().setPage(currentPage);
                currentPage.getShape().setWidth(characters.get(0).getPageWidth());
                currentPage.getShape().setHeight(characters.get(0).getPageHeight());
            }

            for (TextPosition character : characters) {
                textNode.addCharacter(new Character(currentPage, character));
            }

            textNode.setValue(text);

            currentPage.appendChild(textNode);

            super.writeString(text, characters);
        }
    }
}
