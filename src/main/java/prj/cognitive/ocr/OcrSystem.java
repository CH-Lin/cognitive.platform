package prj.cognitive.ocr;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import prj.cognitive.pdf.PdfDocument;

public class OcrSystem {
    private static BlockingQueue<OcrDocument> processingQueue;
    private static BlockingQueue<PdfDocument> resultsQueue;
    private static Thread ocrThread;

    public static void start() {
        if (ocrThread == null) {
            processingQueue = new LinkedBlockingQueue<>();
            resultsQueue = new LinkedBlockingQueue<>();
            ocrThread = new Thread(new OcrEngineRunner(processingQueue, resultsQueue));

            ocrThread.start();
        }
    }

    public static Optional<PdfDocument> ocr(OcrDocument input) {
        try {
            start();
            processingQueue.put(input);
            PdfDocument output = resultsQueue.take();

            if (PdfDocument.EMPTY_DOCUMENT.equals(output)) {
                return Optional.empty();
            }

            return Optional.of(output);
        } catch (InterruptedException exc) {
            return Optional.empty();
        }
    }

    public static void stop() {
        if (ocrThread != null) {
            try {
                processingQueue.put(OcrDocument.STOP_MARKER);
                ocrThread.join();
            } catch (InterruptedException ignore) {

            }
        }
    }
}
