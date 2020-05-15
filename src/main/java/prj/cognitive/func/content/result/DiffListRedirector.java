package prj.cognitive.func.content.result;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import prj.cognitive.func.content.comparison.patch.AbstractDelta;
import prj.cognitive.func.content.comparison.patch.DeltaType;
import prj.cognitive.func.content.comparison.patch.DiffResult;
import prj.cognitive.func.content.elements.Node;

public class DiffListRedirector extends ResultRedirector<DiffResult<Node>> {

	private OutputStream sos;

	public DiffListRedirector(OutputStream sos) {
		this.sos = sos;
	}

	public String getResponseContentType() {
		return "text/plain";
	}

	@Override
	public boolean saveResult(DiffResult<Node> result) {
		try (OutputStreamWriter osw = new OutputStreamWriter(sos)) {
			String str = System.lineSeparator();
			String text = "";
			for (AbstractDelta<Node> delta : result.getDeltas()) {
				DeltaType type = delta.getType();
				switch (type) {
				case CHANGE:
					text += "Changed from: " + delta.getSource().getLines() + " to " + delta.getTarget().getLines();
					break;
				case DELETE:
					text += "Deleted " + delta.getSource().getLines();
					break;
				case INSERT:
					text += "Insertd " + delta.getTarget().getLines();
					break;
				default:
				}
				text += str;
			}
			osw.write(text);
			osw.flush();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
