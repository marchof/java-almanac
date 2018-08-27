package apidiff.model;

import java.util.Set;

import org.objectweb.asm.Opcodes;

public abstract class MemberInfo implements IModelElement<MemberInfo> {

	private static final int IGNORED_ACC = Opcodes.ACC_SYNCHRONIZED | Opcodes.ACC_NATIVE;

	private String name;
	private int access;
	private String desc;

	MemberInfo(String name, int access, String desc) {
		this.name = name;
		this.access = access;
		this.desc = desc;
	}

	public String getName() {
		return name;
	}

	public int getAccess() {
		return access;
	}

	public String getDesc() {
		return desc;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MemberInfo)) {
			return false;
		}
		MemberInfo other = (MemberInfo) obj;
		return name.equals(other.name);
	}

	@Override
	public boolean isModified(MemberInfo other) {
		return (getAccess() | IGNORED_ACC) != (other.getAccess() | IGNORED_ACC);
	}

	@Override
	public String getDiffInfo(MemberInfo other) {
		StringBuilder sb = new StringBuilder();
		Set<String> m1 = Modifiers.getModifiers(this.getAccess());
		Set<String> m2 = Modifiers.getModifiers(other.getAccess());
		m1.stream().filter(m -> !m2.contains(m)).forEach(m -> sb.append(" -" + m));
		m2.stream().filter(m -> !m1.contains(m)).forEach(m -> sb.append(" +" + m));
		return sb.toString();
	}

}
