package apidiff.model;

/**
 * Identity: name
 */
public class FieldInfo extends MemberInfo {

	public FieldInfo(String name, int access, String desc) {
		super(name, access, desc);
	}
	
	@Override
	public String toString() {
		return getName();
	}

}
