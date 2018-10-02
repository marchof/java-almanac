package apidiff.javadoc;

import apidiff.model.ClassInfo;
import apidiff.model.FieldInfo;
import apidiff.model.MethodInfo;
import apidiff.model.PackageInfo;

public class NoJavaDoc implements IJavaDocLinkProvider {

	@Override
	public String getPackageLink(PackageInfo info) {
		return null;
	}

	@Override
	public String getClassLink(ClassInfo info) {
		return null;
	}

	@Override
	public String getMethodLink(MethodInfo info) {
		return null;
	}

	@Override
	public String getFieldLink(FieldInfo info) {
		return null;
	}

}
