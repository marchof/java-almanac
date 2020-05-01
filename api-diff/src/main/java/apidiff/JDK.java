package apidiff;

import apidiff.javadoc.IJavaDocLinkProvider;
import apidiff.javadoc.JavaDoc10;
import apidiff.javadoc.JavaDoc11;
import apidiff.javadoc.JavaDoc;
import apidiff.javadoc.JavaDoc8;
import apidiff.javadoc.JavaDoc9;
import apidiff.javadoc.NoJavaDoc;

public enum JDK {

	V1("1.1", "1.1.8_16-oracle", new NoJavaDoc()),

	V2("1.2", "1.2.2.17-oracle", new JavaDoc("https://javaalmanac.io/jdk/1.2/api/")),

	V3("1.3", "1.3.1.29-oracle", new JavaDoc("https://javaalmanac.io/jdk/1.3/api/")),

	V4("1.4", "1.4.2_30-oracle", new JavaDoc("https://javaalmanac.io/jdk/1.4/api/")),

	V5("5", "1.5.0_22-oracle", new JavaDoc("https://docs.oracle.com/javase/1.5.0/docs/api/")),

	V6("6", "1.6.0_45-oracle", new JavaDoc("https://docs.oracle.com/javase/6/docs/api/")),

	V7("7", "1.7.0_80-oracle", new JavaDoc("https://docs.oracle.com/javase/7/docs/api/")),

	V8("8", "8.0.252.hs-adpt", new JavaDoc8("https://docs.oracle.com/javase/8/docs/api/")),

	V9("9", "9.0.4-open", new JavaDoc9("https://docs.oracle.com/javase/9/docs/api/")),

	V10("10", "10.0.2-open", new JavaDoc10("https://docs.oracle.com/javase/10/docs/api/")),

	V11("11", "11.0.7.hs-adpt", new JavaDoc11("https://docs.oracle.com/en/java/javase/11/docs/api/")),

	V12("12", "12.0.2.hs-adpt", new JavaDoc11("https://docs.oracle.com/en/java/javase/12/docs/api/")),

	V13("13", "13.0.2.hs-adpt", new JavaDoc11("https://docs.oracle.com/en/java/javase/13/docs/api/")),

	V14("14", "14.0.1.hs-adpt", new JavaDoc11("https://docs.oracle.com/en/java/javase/14/docs/api/")),

	V15("15", "15.ea.21-open", new JavaDoc11("https://download.java.net/java/early_access/jdk15/docs/api/"));

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
