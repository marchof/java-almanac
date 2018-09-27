package apidiff.javadoc;

import apidiff.model.ClassInfo;
import apidiff.model.ElementInfo;
import apidiff.model.FieldInfo;
import apidiff.model.MethodInfo;
import apidiff.model.PackageInfo;

public interface IJavaDocLinkProvider {
	
	String getPackageLink(PackageInfo info);
	
	String getClassLink(ClassInfo info);

	String getMethodLink(MethodInfo info);

	String getFieldLink(FieldInfo info);
	
	default String getLink(ElementInfo info) {
		if (info instanceof PackageInfo) {
			return getPackageLink((PackageInfo) info);
		}
		if (info instanceof ClassInfo) {
			return getClassLink((ClassInfo) info);
		}
		if (info instanceof MethodInfo) {
			return getMethodLink((MethodInfo) info);
		}
		if (info instanceof FieldInfo) {
			return getFieldLink((FieldInfo) info);
		}
		throw new IllegalArgumentException("Unsupported type " + info);
	}

	
}
