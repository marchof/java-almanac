package apidiff.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.Opcodes;

/**
 * Identity: name
 */
public class ClassInfo extends ElementInfo {

	private final int access;
	private final String module;

	private Set<FieldInfo> fields;
	private Set<MethodInfo> methods;

	public ClassInfo(String name, int access, String module, String superclass, String[] interfaces) {
		super(name);
		this.access = access;
		this.module = module;
		this.fields = new HashSet<>();
		this.methods = new HashSet<>();
		addTags(ModifierTag.getModifiers(access));
		if (superclass != null) {
			addTag(new ExtendsTag(superclass));
		}
		if (interfaces != null) {
			for (String intf : interfaces) {
				addTag(new ImplementsTag(intf));
			}
		}
	}

	@Override
	public ElementType getType() {
		if ((access & Opcodes.ACC_ANNOTATION) != 0) {
			return ElementType.ANNOTATION;
		}
		if ((access & Opcodes.ACC_INTERFACE) != 0) {
			return ElementType.INTERFACE;
		}
		if ((access & Opcodes.ACC_ENUM) != 0) {
			return ElementType.ENUM;
		}
		return ElementType.CLASS;
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

	public String getDisplayName() {
		String name = getName();
		int idx = name.lastIndexOf('/');
		String simpleName = idx == -1 ? name : name.substring(idx + 1);
		return simpleName.replace('$', '.');
	}

	public String getPackageName() {
		int idx = getName().lastIndexOf('/');
		return idx == -1 ? "<default>" : getName().substring(0, idx);
	}

	public int getAccess() {
		return access;
	}

	public String getModule() {
		return module;
	}

	@Override
	public List<ElementInfo> getChildren() {
		List<ElementInfo> members = new ArrayList<>();
		members.addAll(fields);
		members.addAll(methods);
		return members;
	}

}
