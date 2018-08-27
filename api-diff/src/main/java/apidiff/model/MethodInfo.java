package apidiff.model;

import org.objectweb.asm.Type;

/**
 * Indentity: name, desc
 */
public class MethodInfo extends MemberInfo {

	public MethodInfo(String name, int access, String desc) {
		super(name, access, desc);
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ (31 + getDesc().hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof MethodInfo)) {
			return false;
		}
		MethodInfo other = (MethodInfo) obj;
		return getDesc().equals(other.getDesc());
	}

	@Override
	public String toString() {
		return getMethodName(getName(), getDesc());
	}

	private String getMethodName(final String vmmethodname, final String vmdesc
			) {
		final StringBuilder result = new StringBuilder();
		result.append(vmmethodname);
		result.append('(');
		final Type[] arguments = Type.getArgumentTypes(vmdesc);
		boolean comma = false;
		for (final Type arg : arguments) {
			if (comma) {
				result.append(", ");
			} else {
				comma = true;
			}
			result.append(getShortTypeName(arg));
		}
		result.append(')');
		return result.toString();
	}

	private String getShortTypeName(final Type type) {
		final String name = type.getClassName();
		final int pos = name.lastIndexOf('.');
		final String shortName = pos == -1 ? name : name.substring(pos + 1);
		return shortName.replace('$', '.');
	}

}
