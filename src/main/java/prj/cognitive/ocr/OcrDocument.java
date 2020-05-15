package prj.cognitive.ocr;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import prj.cognitive.pdf.PdfDocument;

public class OcrDocument {
    public static final OcrDocument STOP_MARKER = new OcrDocument(null, null, null);

    private PdfDocument document;
    private String language;
    private String ocrEngine;
    private AbbyyOcrSettings abbyyOcrSettings = new AbbyyOcrSettings();

    public OcrDocument(PdfDocument document, String language, String ocrEngine, AbbyyOcrSettings abbyyOcrSettings) {
        this.document = document;
        this.language = language;
        this.ocrEngine = ocrEngine;
        this.abbyyOcrSettings = abbyyOcrSettings;
    }

    public OcrDocument(PdfDocument document, String language, String ocrEngine) {
        this.document = document;
        this.language = language;
        this.ocrEngine = ocrEngine;
    }

    public PdfDocument getDocument() {
        return document;
    }

    public void setDocument(PdfDocument document) {
        this.document = document;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getOcrEngine() {
        return ocrEngine;
    }

    public void setOcrEngine(String ocrEngine) {
        this.ocrEngine = ocrEngine;
    }

    public AbbyyOcrSettings getAbbyyOcrSettings() {
        return abbyyOcrSettings;
    }

    public void setAbbyyOcrSettings(AbbyyOcrSettings abbyyOcrSettings) {
        this.abbyyOcrSettings = abbyyOcrSettings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OcrDocument)) return false;
        OcrDocument that = (OcrDocument) o;
        return Objects.equal(document, that.document) &&
                Objects.equal(language, that.language) &&
                Objects.equal(ocrEngine, that.ocrEngine) &&
                Objects.equal(abbyyOcrSettings, that.abbyyOcrSettings);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(document, language, ocrEngine, abbyyOcrSettings);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("document", document)
                .add("language", language)
                .add("ocrEngine", ocrEngine)
                .add("abbyyOcrSettings", abbyyOcrSettings)
                .toString();
    }
}
