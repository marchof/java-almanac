package apidiff.javadoc;

import apidiff.model.MethodInfo;

public class JavaDoc11 extends AbstractJavaDoc {

	public JavaDoc11(String baseurl) {
		super(baseurl);
	}

	protected String getPackageBase(String moduleName, String packageName) {
		return baseurl + moduleName + "/" + packageName + "/";
	}

	@Override
	protected String getMethodParameters(MethodInfo info) {
		return "(" + info.getParameterDeclaration(",", false) + ")";
	}

}
