package prj.cognitive.func.content.elements;

import java.util.List;
import java.util.Objects;

import org.apache.pdfbox.text.TextPosition;

public class Word extends Node {
	private String content;

	public Word(Page parent, String content) {
		Objects.requireNonNull(parent, "parent must not be null");
		Objects.requireNonNull(content, "content must not be null");
		this.content = content;
		this.parent = parent;
		this.setArea(Float.MAX_VALUE, Float.MAX_VALUE, 0, 0);
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int expandSize(int currentIdx, final List<TextPosition> textPositions) {
		for (;;) {
			TextPosition text = textPositions.get(currentIdx);
			if (text.getUnicode().charAt(0) == content.charAt(0)) {
				break;
			}
			currentIdx++;
		}

		for (int count = 0; count < content.length(); count++) {
			TextPosition text = textPositions.get(count + currentIdx);
			if (text.getX() < area.x) {
				area.x = text.getX();
			}
			if (text.getY() < area.y) {
				area.y = text.getY();
			}
			if (text.getWidth() > 0) {
				area.width += text.getWidth();
			}
			if (text.getHeight() > 0) {
				area.height = text.getHeight();
			}
		}
		currentIdx += content.length();
		return currentIdx;
	}

	@Override
	public boolean equals(Object anObject) {
		if (this == anObject) {
			return true;
		}
		if (anObject instanceof Word) {
			Word word = (Word) anObject;
			return content.equals(word.content);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return com.google.common.base.Objects.hashCode(content);
	}

	@Override
	public String toString() {
		Page page = (Page) this.getParent();
		return new StringBuffer().append("Word [page=").append(page.getPageNumber()).append(", content=")
				.append(content).append("]").toString();
	}
}
