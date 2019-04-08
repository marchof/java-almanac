package apidiff.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Identity: name
 */
public class ModuleInfo extends ElementInfo {
	
	public static final String UNDEFINED = "undefined";

	private final Set<PackageInfo> packages;

	public ModuleInfo(String name) {
		super(name);
		this.packages = new HashSet<>();
	}

	@Override
	public ElementType getType() {
		return ElementType.MODULE;
	}

	public String getDisplayName() {
		return getName();
	}

	public void addPackage(PackageInfo p) {
		packages.add(p);
	}

	@Override
	public List<ElementInfo> getChildren() {
		return new ArrayList<>(packages);
	}

}
