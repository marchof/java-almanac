package apidiff.model;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.Type;

/**
 * Indentity: name, desc
 */
public class MethodInfo extends MemberInfo {

	private String[] exceptions;

	public MethodInfo(ClassInfo owner, String name, int access, String desc, String[] exceptions) {
		super(owner, name, access, desc);
		this.exceptions = exceptions;
	}

	@Override
	public ElementType getType() {
		return ElementType.METHOD;
	}

	@Override
	public Set<ElementTag> getTags() {
		HashSet<ElementTag> tags = new HashSet<>(ModifierTag.getModifiers(access));
		if (exceptions != null) {
			for (String ex : exceptions) {
				tags.add(new ThrowsTag(ex));
			}
		}
		return tags;
	}

	@Override
	public int hashCode() {
		return name.hashCode() ^ (31 + desc.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MethodInfo)) {
			return false;
		}
		MethodInfo other = (MethodInfo) obj;
		return name.equals(other.name) && desc.equals(other.desc);
	}

	@Override
	public String getName() {
		return getMethodName(name, getDesc());
	}

	private String getMethodName(final String vmmethodname, final String vmdesc) {
		final StringBuilder result = new StringBuilder();
		if ("<init>".equals(vmmethodname)) {
			result.append(getOwner().getName());
		} else {
			result.append(vmmethodname);
		}
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
