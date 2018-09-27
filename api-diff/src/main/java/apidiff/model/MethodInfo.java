package apidiff.model;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Indentity: name, desc
 */
public class MethodInfo extends MemberInfo {

	public MethodInfo(ClassInfo owner, String name, int access, String desc, String[] exceptions) {
		super(owner, name, access, desc);
		addTags(ModifierTag.getModifiers(access));
		if (exceptions != null) {
			for (String ex : exceptions) {
				addTag(new ThrowsTag(ex));
			}
		}
	}

	@Override
	public ElementType getType() {
		return ElementType.METHOD;
	}

	@Override
	public int hashCode() {
		return super.hashCode() * 31 + desc.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		MethodInfo other = (MethodInfo) obj;
		return desc.equals(other.desc);
	}

	@Override
	public String getDisplayName() {
		final StringBuilder result = new StringBuilder();
		if ("<init>".equals(getName())) {
			result.append(getOwner().getDisplayName());
		} else {
			result.append(getName());
		}
		result.append('(');
		result.append(getParameterDeclaration(", ", true));
		result.append(')');
		return result.toString();
	}

	public String getParameterDeclaration(String sep, boolean simpleTypeNames) {
		final StringBuilder result = new StringBuilder();
		final Type[] arguments = Type.getArgumentTypes(getDesc());
		boolean comma = false;
		for (final Type arg : arguments) {
			if (comma) {
				result.append(sep);
			} else {
				comma = true;
			}
			String name = arg.getClassName();
			if (simpleTypeNames) {
				name = getSimpleTypeName(name);
			}
			name = name.replace('$', '.');
			result.append(name);
		}
		if ((getAccess() & Opcodes.ACC_VARARGS) != 0) {
			result.setLength(result.length() - 2);
			result.append("...");
		}
		return result.toString();
	}

	private static String getSimpleTypeName(final String name) {
		final int pos = name.lastIndexOf('.');
		return pos == -1 ? name : name.substring(pos + 1);
	}

}
