package apidiff.javadoc;

import apidiff.model.MethodInfo;

public class JavaDoc10 extends AbstractJavaDoc {

	public JavaDoc10(String baseurl) {
		super(baseurl);
	}

	protected String getPackageBase(String moduleName, String packageName) {
		return baseurl + packageName + "/";
	}

	@Override
	protected String getMethodParameters(MethodInfo info) {
		return "(" + info.getParameterDeclaration(",", false) + ")";
	}

}
