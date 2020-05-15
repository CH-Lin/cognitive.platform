package prj.cognitive.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class PdfSplitter {
    protected static List<Boolean> getIncludedPages(Integer totalPages, String range) {
        List<Boolean> includedPages = new ArrayList<>(totalPages);

        for (int i = 0; i < totalPages; i++) {
            includedPages.add(false);
        }

        String[] ranges = range.split(",");

        for (int i = 0; i < ranges.length; i++) {
            String[] subrange = ranges[i].split("-");
            int start = Integer.parseInt(subrange[0].trim());
            int end = subrange.length == 2 ? Integer.parseInt(subrange[1].trim()) : start;

            for (int j = start; j <= end; j++) {
                includedPages.set(j - 1, true);
            }
        }

        return includedPages;
    }

    public static void split(PdfDocument document, String range, OutputStream stream) throws IOException {
        List<Boolean> pages = getIncludedPages(document.getDocument().getNumberOfPages(), range);

        PDDocument outputDocument = new PDDocument();
        for (int i = 0; i < document.getDocument().getNumberOfPages(); i++) {
            if (pages.get(i)) {
                outputDocument.addPage(document.getDocument().getPage(i));
            }
        }

        outputDocument.save(stream);
        outputDocument.close();
    }
}
