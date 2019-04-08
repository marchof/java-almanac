package apidiff.javadoc;

import apidiff.model.MethodInfo;
import apidiff.model.ModuleInfo;

public class JavaDoc11 extends AbstractJavaDoc {

	public JavaDoc11(String baseurl) {
		super(baseurl);
	}
	
	@Override
	public String getModuleLink(ModuleInfo info) {
		return  baseurl + info.getName() + "/module-summary.html";
	}

	protected String getPackageBase(String moduleName, String packageName) {
		return baseurl + moduleName + "/" + packageName + "/";
	}

	@Override
	protected String getMethodParameters(MethodInfo info) {
		return "(" + info.getParameterDeclaration(",", false) + ")";
	}

}
