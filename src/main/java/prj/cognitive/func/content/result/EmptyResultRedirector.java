package prj.cognitive.func.content.result;

public class EmptyResultRedirector extends ResultRedirector<Result> {

	@Override
	public String getResponseContentType() {
		return null;
	}

	@Override
	public boolean saveResult(Result result) {
		return false;
	}

}
