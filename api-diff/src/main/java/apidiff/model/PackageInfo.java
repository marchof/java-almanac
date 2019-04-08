package apidiff.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Identity: name
 */
public class PackageInfo extends ElementInfo {

	private final Set<ClassInfo> classes;

	private final String module;

	public PackageInfo(String name, String module) {
		super(name);
		this.module = module;
		this.classes = new HashSet<>();
	}

	@Override
	public ElementType getType() {
		return ElementType.PACKAGE;
	}

	public String getDisplayName() {
		return getName().replace('/', '.');
	}
	
	public String getModule() {
		return module;
	}

	public void addClass(ClassInfo c) {
		if (!classes.add(c)) {
			throw new IllegalStateException("Duplicate class " + c);
		}
	}

	@Override
	public List<ElementInfo> getChildren() {
		return new ArrayList<>(classes);
	}

}
