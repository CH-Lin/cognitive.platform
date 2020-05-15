package prj.cognitive.func.content.elements;

public class Page extends Node {
	private int pageNumber;

	public Page(Document parent) {
		super(parent);
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
}
