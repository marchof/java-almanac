package apidiff.javadoc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;

import apidiff.model.ClassInfo;
import apidiff.model.FieldInfo;
import apidiff.model.MethodInfo;
import apidiff.model.ModuleInfo;
import apidiff.model.PackageInfo;

abstract class JavaDocTestBase {
	
	protected IJavaDocLinkProvider javadoc;

	protected String link_module;

	protected String link_package;

	protected String link_class;

	protected String link_inner_class;
	
	protected String link_field;
	
	protected String link_constructor;
	
	protected String link_method_parameters;
	
	protected String link_method_array;
	
	protected String link_method_varargs;

	@Test
	public void link_module() {
		ModuleInfo info = new ModuleInfo("java.base");
		assertEquals(link_module, javadoc.getModuleLink(info));
	}
	
	@Test
	public void link_package() {
		PackageInfo info = new PackageInfo("java/lang", "java.base");
		assertEquals(link_package, javadoc.getPackageLink(info));
	}
	
	@Test
	public void link_class() {
		ClassInfo info = new ClassInfo("java/lang/Object", 0, "java.base", null, null);
		assertEquals(link_class, javadoc.getClassLink(info));
	}
	
	@Test
	public void link_inner_class() {
		ClassInfo info = new ClassInfo("java/lang/Thread$State", 0, "java.base", null, null);
		assertEquals(link_inner_class, javadoc.getClassLink(info));
	}
	
	@Test
	public void link_field() {
		ClassInfo owner = new ClassInfo("java/lang/Long", 0, "java.base", null, null);
		FieldInfo info = new FieldInfo(owner, "BYTES", 0, null);
		assertEquals(link_field, javadoc.getFieldLink(info));
	}

	@Test
	public void link_constructor() {
		ClassInfo owner = new ClassInfo("java/lang/Thread", 0, "java.base", null, null);
		MethodInfo info = new MethodInfo(owner, "<init>", 0, "()V", null);
		assertEquals(link_constructor, javadoc.getMethodLink(info));
	}
	
	@Test
	public void link_method_parameters() {
		ClassInfo owner = new ClassInfo("java/lang/Math", 0, "java.base", null, null);
		MethodInfo info = new MethodInfo(owner, "max", 0, "(DD)V", null);
		assertEquals(link_method_parameters, javadoc.getMethodLink(info));
	}
	
	@Test
	public void link_method_array() {
		ClassInfo owner = new ClassInfo("java/lang/Runtime", 0, "java.base", null, null);
		MethodInfo info = new MethodInfo(owner, "exec", 0, "([Ljava/lang/String;)V", null);
		assertEquals(link_method_array, javadoc.getMethodLink(info));
	}
	
	@Test
	public void link_method_varargs() {
		ClassInfo owner = new ClassInfo("java/lang/String", 0, "java.base", null, null);
		MethodInfo info = new MethodInfo(owner, "format", Opcodes.ACC_VARARGS, "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", null);
		assertEquals(link_method_varargs, javadoc.getMethodLink(info));
	}

}
