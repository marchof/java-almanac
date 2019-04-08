package apidiff.javadoc;

import apidiff.model.MethodInfo;
import apidiff.model.ModuleInfo;

public class JavaDoc10 extends AbstractJavaDoc {

	public JavaDoc10(String baseurl) {
		super(baseurl);
	}
	
	@Override
	public String getModuleLink(ModuleInfo info) {
		return  baseurl + info.getName() + "-summary.html";
	}

	protected String getPackageBase(String moduleName, String packageName) {
		return baseurl + packageName + "/";
	}

	@Override
	protected String getMethodParameters(MethodInfo info) {
		return "(" + info.getParameterDeclaration(",", false) + ")";
	}

}
