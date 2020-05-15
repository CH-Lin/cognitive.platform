package prj.cognitive.ocr;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import prj.cognitive.pdf.PdfDocument;

public class OcrEngineRunner implements Runnable {
    private BlockingQueue<OcrDocument> queue;
    private BlockingQueue<PdfDocument> outputQueue;

    public OcrEngineRunner(BlockingQueue<OcrDocument> inputQueue, BlockingQueue<PdfDocument> outputQueue) {
        this.queue = inputQueue;
        this.outputQueue = outputQueue;
    }

    protected Map<String, OcrEngine> loadEngines() {
        Map<String, OcrEngine> ocrEngines = new HashMap<>();
        ocrEngines.put(AbbyyOcrEngine.CODE, new AbbyyOcrEngine());
        return ocrEngines;
    }

    @Override
    public void run() {
        Map<String, OcrEngine> ocrEngines = loadEngines();

        try {
            OcrDocument input = queue.take();
            while (!OcrDocument.STOP_MARKER.equals(input)) {
                if (ocrEngines.containsKey(input.getOcrEngine())) {
                    PdfDocument output = ocrEngines.get(input.getOcrEngine()).process(input);

                    outputQueue.put(output);
                } else {
                    outputQueue.put(PdfDocument.EMPTY_DOCUMENT); // TODO Document this
                }

                input = queue.take();
            }
        } catch (Exception exc) {
            exc.printStackTrace();
            try {
                outputQueue.put(PdfDocument.EMPTY_DOCUMENT);
            } catch (InterruptedException ignore) {

            }
        }
    }
}
