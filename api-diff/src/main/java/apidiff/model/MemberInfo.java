package apidiff.model;

public abstract class MemberInfo extends ElementInfo {

	protected final int access;
	protected final String desc;
	private ClassInfo owner;

	MemberInfo(ClassInfo owner, String name, int access, String desc) {
		super(name);
		this.owner = owner;
		this.access = access;
		this.desc = desc;
	}

	public ClassInfo getOwner() {
		return owner;
	}
	
	public int getAccess() {
		return access;
	}

	public String getDesc() {
		return desc;
	}

}
