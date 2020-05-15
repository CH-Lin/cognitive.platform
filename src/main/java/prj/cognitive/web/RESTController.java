package prj.cognitive.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import prj.cognitive.ocr.AbbyyOcrEngine;
import prj.cognitive.ocr.OcrDocument;
import prj.cognitive.ocr.OcrSystem;
import prj.cognitive.pdf.PdfDocument;
import prj.cognitive.pdf.elements.Text;

@RestController
public class RESTController {

	@RequestMapping("/")
	public void test() {

	}

	@RequestMapping(value = "/pdf/analysis", method = RequestMethod.GET)
	public List<Text> analysis(@RequestPart(value = "file", required = false) MultipartFile request,
			@RequestParam(value = "language", required = true) String language, @RequestParam("query") String query) {
		try (InputStream rawInput = request.getInputStream()) {
			try (PdfDocument input = new PdfDocument(rawInput)) {
				PdfDocument searchDoc = input;
				searchDoc = OcrSystem.ocr(new OcrDocument(input, language, AbbyyOcrEngine.CODE)).orElse(null);
				return searchDoc.getStructuredDocument().search(query);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping(value = "/pdf/search", method = RequestMethod.GET)
	public List<Text> search(@RequestPart(value = "file", required = false) MultipartFile request,
			@RequestParam(value = "language", required = true) String language, @RequestParam("query") String query) {
		try (InputStream rawInput = request.getInputStream()) {
			try (PdfDocument input = new PdfDocument(rawInput)) {
				PdfDocument searchDoc = input;
				searchDoc = OcrSystem.ocr(new OcrDocument(input, language, AbbyyOcrEngine.CODE)).orElse(null);
				return searchDoc.getStructuredDocument().search(query);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
