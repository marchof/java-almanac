package apidiff.model;

public class ImplementsTag implements ElementTag {

	private String intf;

	public ImplementsTag(String intf) {
		this.intf = intf;
	}

	@Override
	public String toString() {
		int idx = intf.lastIndexOf('/');
		String shortName = idx == -1 ? intf : intf.substring(idx + 1);
		return "implements " + shortName.replace('$', '.');
	}

	@Override
	public int hashCode() {
		return intf.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ImplementsTag)) {
			return false;
		}
		ImplementsTag other = (ImplementsTag) obj;
		return intf.equals(other.intf);
	}

}