package apidiff.javadoc;

import org.junit.jupiter.api.BeforeEach;

public class JavaDoc10Test extends JavaDocTestBase {
	
	@BeforeEach
	public void before() {
		javadoc = new JavaDoc10("http://javadoc.org/");
		link_module = "http://javadoc.org/java.base-summary.html";
		link_package = "http://javadoc.org/java/lang/package-summary.html";
		link_class = "http://javadoc.org/java/lang/Object.html";
		link_inner_class = "http://javadoc.org/java/lang/Thread.State.html";
		link_field = "http://javadoc.org/java/lang/Long.html#BYTES";
		link_constructor = "http://javadoc.org/java/lang/Thread.html#<init>()";
		link_method_parameters ="http://javadoc.org/java/lang/Math.html#max(double,double)";
		link_method_array = "http://javadoc.org/java/lang/Runtime.html#exec(java.lang.String[])";
		link_method_varargs = "http://javadoc.org/java/lang/String.html#format(java.lang.String,java.lang.Object...)";
	}

}
