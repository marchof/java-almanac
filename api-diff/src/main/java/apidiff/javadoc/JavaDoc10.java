package apidiff.javadoc;

import apidiff.model.ClassInfo;
import apidiff.model.FieldInfo;
import apidiff.model.MethodInfo;
import apidiff.model.PackageInfo;

public class JavaDoc10 implements IJavaDocLinkProvider {

	private String baseurl;

	public JavaDoc10(String baseurl) {
		this.baseurl = baseurl;
	}

	private String getPackageBase(String moduleName, String packageName) {
		return baseurl + packageName + "/";
	}

	@Override
	public String getPackageLink(PackageInfo info) {
		return getPackageBase(info.getModule(), info.getName()) + "package-summary.html";
	}

	@Override
	public String getClassLink(ClassInfo info) {
		return getPackageBase(info.getModule(), info.getPackageName()) + info.getDisplayName() + ".html";
	}

	@Override
	public String getMethodLink(MethodInfo info) {
		StringBuilder sb = new StringBuilder();
		sb.append(getClassLink(info.getOwner()));
		sb.append("#");
		sb.append(info.getName());
		sb.append("(");
		sb.append(info.getParameterDeclaration(",", false));
		sb.append(")");
		return sb.toString();
	}

	@Override
	public String getFieldLink(FieldInfo info) {
		return getClassLink(info.getOwner()) + "#" + info.getName();
	}

}
