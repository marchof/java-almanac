package apidiff.model;

public class ThrowsTag implements ElementTag {

	private String exception;

	public ThrowsTag(String exception) {
		this.exception = exception;
	}

	@Override
	public String toString() {
		int idx = exception.lastIndexOf('/');
		return "throws " + (idx == -1 ? exception : exception.substring(idx + 1));
	}

	@Override
	public int hashCode() {
		return exception.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ThrowsTag)) {
			return false;
		}
		ThrowsTag other = (ThrowsTag) obj;
		return exception.equals(other.exception);
	}

}