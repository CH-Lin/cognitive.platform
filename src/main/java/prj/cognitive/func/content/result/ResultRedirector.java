package prj.cognitive.func.content.result;

public abstract class ResultRedirector<T> {
	public abstract String getResponseContentType();

	public abstract boolean saveResult(T result);
}
