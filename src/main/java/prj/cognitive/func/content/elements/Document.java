package prj.cognitive.func.content.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.pdfbox.text.TextPosition;

public class Document extends Node {

	private Page currentPage;

	public Document() {
	}

	public void addNewPage(int pageNumber, float x, float y, float w, float h) {
		currentPage = new Page(this);
		currentPage.setPageNumber(pageNumber);
		currentPage.setArea(x, y, w, h);
		this.addChild(currentPage);
	}

	public void endOfPage() {
		// Skip now
	}

	public Page getPage(int pageNumber) {
		return (Page) getChildren().get(pageNumber - 1);
	}

	public int getPageCount() {
		return children.size();
	}

	public void addContent(final String text, final List<TextPosition> characters) {
		assert (text != null);
		int currentIdx = 0;
		StringTokenizer st = new StringTokenizer(text);
		while (st.hasMoreElements()) {
			Word w = new Word(currentPage, st.nextToken());
			currentIdx = w.expandSize(currentIdx, characters);
			currentPage.addChild(w);
		}
	}

	public List<Node> getAllWords() {
		List<Node> words = new ArrayList<>();
		for (Node page : this.getChildren()) {
			if (page instanceof Page) {
				words.addAll(page.getChildren());
			}
		}
		return words;
	}
}
