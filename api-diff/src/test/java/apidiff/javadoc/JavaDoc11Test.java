package apidiff.javadoc;

import org.junit.jupiter.api.BeforeEach;

public class JavaDoc11Test extends JavaDocTestBase {
	
	@BeforeEach
	public void before() {
		javadoc = new JavaDoc11("http://javadoc.org/");
		link_module = "http://javadoc.org/java.base/module-summary.html";
		link_package = "http://javadoc.org/java.base/java/lang/package-summary.html";
		link_class = "http://javadoc.org/java.base/java/lang/Object.html";
		link_inner_class = "http://javadoc.org/java.base/java/lang/Thread.State.html";
		link_field = "http://javadoc.org/java.base/java/lang/Long.html#BYTES";
		link_constructor = "http://javadoc.org/java.base/java/lang/Thread.html#%3Cinit%3E()";
		link_method_parameters ="http://javadoc.org/java.base/java/lang/Math.html#max(double,double)";
		link_method_array = "http://javadoc.org/java.base/java/lang/Runtime.html#exec(java.lang.String%5B%5D)";
		link_method_varargs = "http://javadoc.org/java.base/java/lang/String.html#format(java.lang.String,java.lang.Object...)";
	}

}
