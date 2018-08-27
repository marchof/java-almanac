package apidiff.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class ApiInfo implements Consumer<ClassInfo>, IModelElement<ApiInfo> {

	private final Map<String, PackageInfo> packages;

	public ApiInfo() {
		this.packages = new HashMap<>();
	}

	public Set<PackageInfo> getPackages() {
		return new HashSet<>(packages.values());
	}

	@Override
	public void accept(ClassInfo c) {
		PackageInfo pkg = packages.computeIfAbsent(c.getPackageName(), PackageInfo::new);
		pkg.addClass(c);
	}

	@Override
	public List<IModelElement<?>> getChildren() {
		List<IModelElement<?>> children = new ArrayList<>();
		children.addAll(packages.values());
		return children;
	}

}
