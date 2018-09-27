package apidiff.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Identity: vmname
 */
public class PackageInfo extends ElementInfo {

	private final Set<ClassInfo> classes;

	private final String vmname;

	private final String module;

	PackageInfo(String vmname, String module) {
		this.vmname = vmname;
		this.module = module;
		this.classes = new HashSet<>();
	}

	@Override
	public ElementType getType() {
		return ElementType.PACKAGE;
	}
	
	public String getVMName() {
		return vmname;
	}

	public String getName() {
		return vmname.replace('/', '.');
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
	public int hashCode() {
		return vmname.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PackageInfo)) {
			return false;
		}
		PackageInfo other = (PackageInfo) obj;
		return vmname.equals(other.vmname);
	}

	@Override
	public List<ElementInfo> getChildren() {
		List<ElementInfo> children = new ArrayList<>();
		children.addAll(classes);
		return children;
	}

}
