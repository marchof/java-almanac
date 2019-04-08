package apidiff.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ApiInfo extends ElementInfo implements Consumer<ClassInfo> {

	private final Map<String, PackageInfo> packages;
	private final Map<String, ModuleInfo> modules;
	private final String detail;

	public ApiInfo(String name, String detail) {
		super(name);
		this.detail = detail;
		this.packages = new HashMap<>();
		this.modules = new HashMap<>();
	}
	
	private ApiInfo(String name, String detail, Map<String, PackageInfo> packages) {
		super(name);
		this.detail = detail;
		this.packages = packages;
		this.modules = new HashMap<>();
	}

	public String getDetail() {
		return detail;
	}
	
	public boolean hasModules() {
		return !modules.isEmpty();
	}
	
	public ApiInfo withoutModules() {
		return new ApiInfo(getName(), detail, packages);
	}

	@Override
	public ElementType getType() {
		return ElementType.API;
	}

	@Override
	public String getDisplayName() {
		return getName();
	}

	@Override
	public void accept(ClassInfo c) {
		PackageInfo pkg = packages.computeIfAbsent(c.getPackageName(), name -> createPackage(name, c.getModule()));
		pkg.addClass(c);
	}

	private PackageInfo createPackage(String name, String module) {
		PackageInfo pkg = new PackageInfo(name, module);
		if (!ModuleInfo.UNDEFINED.equals(module)) {
			modules.computeIfAbsent(module, ModuleInfo::new).addPackage(pkg);
		}
		return pkg;
	}

	@Override
	public List<ElementInfo> getChildren() {
		return new ArrayList<>(hasModules() ? modules.values() : packages.values());
	}

}
