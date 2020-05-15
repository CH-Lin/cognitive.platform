package prj.cognitive.processor;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import prj.cognitive.ocr.AbbyyOcrEngine;
import prj.cognitive.ocr.OcrDocument;
import prj.cognitive.ocr.OcrSystem;
import prj.cognitive.pdf.PdfDocument;
import prj.cognitive.utils.Config;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class DocumentProcessor {
    private static final Double MAX_GROUP_THRESHOLD = Config.get(
            DocumentProcessor.class, "MAX_GROUP_THRESHOLD", 10 * ProcessedDocument.MISSING_LABEL_WEIGHT
    );

    private static final String DISABLE_OCR_LANGUAGE = Config.get(
            DocumentProcessor.class, "DISABLE_OCR_LANGUAGE", "none"
    );

    private PdfDocument document;
    private DocumentConfiguration configuration;
    private LoadingCache<String, ProcessedDocument> groups = CacheBuilder.newBuilder()
            .maximumSize(50)
            .build(new CacheLoader<String, ProcessedDocument>() {
                @Override
                public ProcessedDocument load(String s) throws Exception {
                    return new ProcessedDocument(s, document, configuration.getGroups().get(s));
                }
            });

    public DocumentProcessor(PdfDocument document, DocumentConfiguration configuration) {
        this.document = document;
        this.configuration = configuration;
    }

    public void ocr() {
        if (!DISABLE_OCR_LANGUAGE.equals(configuration.getLanguages())) {
            this.document = OcrSystem.ocr(new OcrDocument(
                    document,
                    configuration.getLanguages(),
                    AbbyyOcrEngine.CODE
            )).orElse(null);
        }
    }

    public ProcessedDocument getDocument(String groupName) {
        if (groupName == null) {
            return null;
        }

        try {
            return groups.get(groupName);
        } catch (ExecutionException exc) {
            return null;
        }
    }

    public PdfDocument getOriginalDocument() {
        return document;
    }

    public DocumentConfiguration getConfiguration() {
        return configuration;
    }

    public Optional<String> classifyDocument() {
        return configuration.getGroups().keySet().stream().map(this::getDocument).filter(
                group -> group.getScore() < MAX_GROUP_THRESHOLD
        ).sorted().map(ProcessedDocument::getGroupName).findFirst();
    }
}
