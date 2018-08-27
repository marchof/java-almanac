package apidiff.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Identity: name
 */
public class PackageInfo implements IModelElement<PackageInfo> {

	private final Set<ClassInfo> classes;

	private final String name;

	PackageInfo(String name) {
		this.name = name;
		this.classes = new HashSet<>();
	}

	public String getName() {
		return name;
	}

	public void addClass(ClassInfo c) {
		if (!classes.add(c)) {
			throw new IllegalStateException("Duplicate class " + c);
		}
	}

	public Set<ClassInfo> getClasses() {
		return classes;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PackageInfo)) {
			return false;
		}
		PackageInfo other = (PackageInfo) obj;
		return name.equals(other.name);
	}

	@Override
	public String toString() {
		return name.replace('/', '.');
	}
	
	@Override
	public List<IModelElement<?>> getChildren() {
		List<IModelElement<?>> children = new ArrayList<>();
		children.addAll(classes);
		return children;
	}

}
