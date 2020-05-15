package prj.cognitive.ocr;

import java.io.Closeable;

import prj.cognitive.pdf.PdfDocument;

public abstract class OcrEngine implements Closeable {
    public abstract PdfDocument process(OcrDocument document) throws Exception;

    public abstract void close();
}
