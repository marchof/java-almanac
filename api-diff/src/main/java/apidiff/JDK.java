package apidiff;

import apidiff.javadoc.IJavaDocLinkProvider;
import apidiff.javadoc.JavaDoc10;
import apidiff.javadoc.JavaDoc11;
import apidiff.javadoc.JavaDoc9;
import apidiff.javadoc.NoJavaDoc;

public enum JDK {

	V8("Java 8", "8.0.201-oracle", new NoJavaDoc()),
	
	V9("Java 9", "9.0.4-open", new JavaDoc9("https://docs.oracle.com/javase/9/docs/api/")),
	
	V10("Java 10", "10.0.2-open", new JavaDoc10("https://docs.oracle.com/javase/10/docs/api/")),
	
	V11("Java 11", "11.0.2-open", new JavaDoc11("https://docs.oracle.com/en/java/javase/11/docs/api/")),
	
	V12("Java 12", "12.0.0-open", new JavaDoc11("https://docs.oracle.com/en/java/javase/12/docs/api/")),

	V13("Java 13", "13.ea.11-open", new JavaDoc11("https://cr.openjdk.java.net/~iris/se/13/build/latest/api/"));

	private String name;
	private String impl;
	private IJavaDocLinkProvider doc;

	private JDK(String name, String impl, IJavaDocLinkProvider doc) {
		this.name = name;
		this.impl = impl;
		this.doc = doc;
	}
	
	public String getName() {
		return name;
	}
	
	public String getImpl() {
		return impl;
	}
	
	public IJavaDocLinkProvider getDoc() {
		return doc;
	}
	
}
