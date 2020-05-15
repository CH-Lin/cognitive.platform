package prj.cognitive.content.wrapper;

import java.io.IOException;

import prj.cognitive.func.content.elements.Document;

public interface DocWrapper {
	public Document extractContent() throws IOException;
}
