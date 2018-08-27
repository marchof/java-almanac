package apidiff.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.Opcodes;

/**
 * Identity: name
 */
public class ClassInfo implements IModelElement<ClassInfo> {

	private String name;
	private int access;

	private Set<FieldInfo> fields;
	private Set<MethodInfo> methods;

	public ClassInfo(String name, int access) {
		this.name = name;
		this.access = access;
		this.fields = new HashSet<>();
		this.methods = new HashSet<>();
	}

	public void addField(FieldInfo f) {
		if (!fields.add(f)) {
			throw new IllegalStateException("Duplicate field " + f);
		}
	}

	public void addMethod(MethodInfo m) {
		if (!methods.add(m)) {
			throw new IllegalStateException("Duplicate method " + m);
		}
	}

	public String getName() {
		return name;
	}

	public String getPackageName() {
		int idx = name.lastIndexOf('/');
		return idx == -1 ? "<default>" : name.substring(0, idx);
	}

	public int getAccess() {
		return access;
	}

	public Set<FieldInfo> getFields() {
		return fields;
	}

	public Set<MethodInfo> getMethods() {
		return methods;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ClassInfo)) {
			return false;
		}
		ClassInfo other = (ClassInfo) obj;
		return name.contentEquals(other.name);
	}

	@Override
	public String toString() {
		int idx = name.lastIndexOf('/');
		String simpleName = idx == -1 ? name : name.substring(idx + 1);
		return getKind() + " " + simpleName.replace('$', '.');
	}

	private String getKind() {
		if ((getAccess() & Opcodes.ACC_INTERFACE) != 0) {
			return "interface";
		}
		if ((getAccess() & Opcodes.ACC_ENUM) != 0) {
			return "enum";
		}
		if ((getAccess() & Opcodes.ACC_ANNOTATION) != 0) {
			return "annotation";
		}
		return "class";
	}

	@Override
	public List<IModelElement<?>> getChildren() {
		List<IModelElement<?>> members = new ArrayList<>();
		members.addAll(fields);
		members.addAll(methods);
		return members;
	}

	@Override
	public boolean isModified(ClassInfo other) {
		return access != other.access;
	}

}
