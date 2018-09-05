package apidiff.model;

public class ExtendsTag implements ElementTag {

	private String superclass;

	public ExtendsTag(String superclass) {
		this.superclass = superclass;
	}

	@Override
	public String toString() {
		int idx = superclass.lastIndexOf('/');
		return "extends " + (idx == -1 ? superclass : superclass.substring(idx + 1));
	}

	@Override
	public int hashCode() {
		return superclass.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ExtendsTag)) {
			return false;
		}
		ExtendsTag other = (ExtendsTag) obj;
		return superclass.equals(other.superclass);
	}

}