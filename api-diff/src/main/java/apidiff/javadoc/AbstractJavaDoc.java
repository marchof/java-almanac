package apidiff.javadoc;

import apidiff.model.ClassInfo;
import apidiff.model.FieldInfo;
import apidiff.model.MethodInfo;
import apidiff.model.PackageInfo;

abstract class AbstractJavaDoc implements IJavaDocLinkProvider {

	protected String baseurl;

	protected AbstractJavaDoc(String baseurl) {
		this.baseurl = baseurl;
	}

	protected abstract String getPackageBase(String moduleName, String packageName);

	@Override
	public String getPackageLink(PackageInfo info) {
		return getPackageBase(info.getModule(), info.getName()) + "package-summary.html";
	}

	@Override
	public String getClassLink(ClassInfo info) {
		return getPackageBase(info.getModule(), info.getPackageName()) + info.getDisplayName() + ".html";
	}

	@Override
	public String getFieldLink(FieldInfo info) {
		return getClassLink(info.getOwner()) + "#" + info.getName();
	}

	@Override
	public String getMethodLink(MethodInfo info) {
		StringBuilder sb = new StringBuilder();
		sb.append(getClassLink(info.getOwner()));
		sb.append("#");
		sb.append(getMethodName(info));
		sb.append(getMethodParameters(info));
		return sb.toString();
	}

	protected String getMethodName(MethodInfo info) {
		return info.getName();
	}

	protected abstract String getMethodParameters(MethodInfo info);

}
