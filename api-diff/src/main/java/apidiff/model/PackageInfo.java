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

	private final String name;

	PackageInfo(String name) {
		this.name = name;
		this.classes = new HashSet<>();
	}

	@Override
	public ElementType getType() {
		return ElementType.PACKAGE;
	}

	public String getName() {
		return name.replace('/', '.');
	}

	public void addClass(ClassInfo c) {
		if (!classes.add(c)) {
			throw new IllegalStateException("Duplicate class " + c);
		}
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
	public List<ElementInfo> getChildren() {
		List<ElementInfo> children = new ArrayList<>();
		children.addAll(classes);
		return children;
	}

}
