package prj.cognitive.pdf;

import com.google.common.base.Objects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import prj.cognitive.pdf.elements.Document;
import prj.cognitive.pdf.elements.attrs.PositionalArea;
import prj.cognitive.utils.Config;
import prj.cognitive.utils.ImageExtraction;

import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.concurrent.ExecutionException;

public class PdfDocument implements Closeable {
    public static final PdfDocument EMPTY_DOCUMENT = new PdfDocument();
    protected Integer DEFAULT_DPI = Config.get(
            PdfDocument.class, "DEFAULT_DPI", 144
    );
    private PDFRenderer renderer;
    private PDDocument document;
    private ByteSource documentData;
    private Document structuredDocument;
    private Integer dpi = DEFAULT_DPI;
    private LoadingCache<Integer, BufferedImage> images = CacheBuilder.newBuilder()
            .maximumSize(25)
            .build(new CacheLoader<Integer, BufferedImage>() {
                @Override
                public BufferedImage load(Integer pageNumber) throws Exception {
                    return renderer.renderImageWithDPI(pageNumber - 1, dpi);
                }
            });

    public PdfDocument() {

    }

    public PdfDocument(File file) throws IOException {
        documentData = Files.asByteSource(file);
        parsePDF(new RandomAccessFile(file, "r"));
    }

    public PdfDocument(InputStream stream) throws IOException {
        documentData = ByteSource.wrap(ByteStreams.toByteArray(stream));
        parsePDF(new RandomAccessBuffer(documentData.openStream()));
    }

    public void setDpi(int dpi) {
        this.dpi = dpi;
    }

    private void parsePDF(RandomAccessRead reader) throws IOException {
        PDFParser parser = new PDFParser(reader);
        parser.parse();
        document = parser.getPDDocument();
        renderer = new PDFRenderer(document);
        StructuredPdfBuilder builder = new StructuredPdfBuilder(document);
        structuredDocument = builder.getStructuredDocument();
    }

    public PDDocument getDocument() {
        return document;
    }

    public Document getStructuredDocument() {
        return structuredDocument;
    }

    public BufferedImage renderPage(int page) throws IOException {
        try {
            return images.get(page);
        } catch (ExecutionException exc) {
            throw new IOException(exc.getCause());
        }
    }

    public BufferedImage renderRegion(PositionalArea region, boolean enlargeToBorders) throws IOException {
        BufferedImage image = renderPage(region.getPage().getPageNumber());
        double scaleX = image.getWidth() / region.getPage().getShape().getWidth();
        double scaleY = image.getHeight() / region.getPage().getShape().getHeight();
        PositionalArea scaledPage = region.scalePage(scaleX, scaleY);
        PositionalArea scaled = scaledPage.scaleTo(scaleX, scaleY);

        if (enlargeToBorders) {
            scaled = ImageExtraction.enlargeToBorders(scaled, image);
        }

        return image.getSubimage(
                (int) Math.round(scaled.getX()),
                (int) Math.round(scaled.getY()),
                (int) Math.round(scaled.getWidth()),
                (int) Math.round(scaled.getHeight())
        );
    }

    public void highlight(int page, Color color, double opacity, double x, double y, double width, double height) throws IOException {
        PDPageContentStream contentStream = new PDPageContentStream(
                document,
                document.getPage(page - 1),
                PDPageContentStream.AppendMode.APPEND,
                true);
        PDExtendedGraphicsState graphics = new PDExtendedGraphicsState();
        graphics.setNonStrokingAlphaConstant((float) opacity);
        contentStream.setGraphicsStateParameters(graphics);

        contentStream.setNonStrokingColor(color);
        contentStream.addRect(
                (float) x,
                (float) (document.getPage(page - 1).getBleedBox().getHeight() - height - y),
                (float) width,
                (float) height
        );
        contentStream.fill();
        contentStream.close();

        System.out.println("HIGH LIGHT");
    }

    public void addText(String text, int page, Color color, double opacity, double x, double y, String fontName, double fontSize) throws IOException {
        PDPageContentStream contentStream = new PDPageContentStream(
                document,
                document.getPage(page - 1),
                PDPageContentStream.AppendMode.APPEND,
                true);

        contentStream.beginText();
        contentStream.setNonStrokingColor(color);
		if (fontName == null) {
			contentStream.setFont(PDType1Font.HELVETICA, (float) fontSize);
		} else {
			try (FileInputStream fis = new FileInputStream("font" + File.separator + fontName)) {
				PDFont font = PDType0Font.load(document, fis, false);
				contentStream.setFont(font, (float) fontSize);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
        contentStream.newLineAtOffset((float) x, (float) (document.getPage(page - 1).getBleedBox().getHeight() - fontSize - y));
        contentStream.showText(text);
        contentStream.endText();
        contentStream.close();

        System.out.println("ADD TEXT");
    }

    public void annotate(PdfAnnotation annotation) throws IOException {
        // TODO: Combine highlight and addText for greater speed
        if (annotation.getBackgroundColor() != null) {
            highlight(
                    annotation.getPage(), annotation.getBackgroundColor(),
                    annotation.getOpacity(), annotation.getX(), annotation.getY(),
                    annotation.getWidth(), annotation.getHeight()
            );
        }

        if (annotation.getText() != null) {
            addText(
                    annotation.getText(), annotation.getPage(), annotation.getForegroundColor(),
                    annotation.getOpacity(), annotation.getX(), annotation.getY(),
                    annotation.getFontName(), annotation.getFontSize()
            );
        }
    }

    public ByteSource getDocumentData() {
        return documentData;
    }

    public ByteArrayOutputStream outputPDF() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        return (ByteArrayOutputStream) outputPDF(outputStream);
    }

    public OutputStream outputPDF(OutputStream outputStream) throws IOException {
        document.save(outputStream);
        return outputStream;
    }

    public void close() {
        if (document != null) {
            try {
                document.close();
            } catch (Exception ignore) {

            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PdfDocument)) return false;
        PdfDocument document1 = (PdfDocument) o;
        return Objects.equal(document, document1.document) &&
                Objects.equal(documentData, document1.documentData);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(document, documentData);
    }
}
