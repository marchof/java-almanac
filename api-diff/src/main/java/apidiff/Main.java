package apidiff;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import apidiff.cmp.Delta;
import apidiff.loader.Loader;
import apidiff.loader.PublicApiFilter;
import apidiff.model.ApiInfo;
import apidiff.report.FileMultiReportOutput;
import apidiff.report.HTMLRenderer;

public class Main {

	private static ApiInfo loadVersion(JDK jdk) throws IOException {
		ApiInfo api = new ApiInfo(jdk.getName(), jdk.getImpl());
		Loader loader = new Loader(api, new PublicApiFilter());
		loader.loadJDK(Paths.get(System.getProperty("user.home"), ".sdkman/candidates/java", jdk.getImpl()));
		return api;
	}

	private static void createReport(JDK a, JDK b) throws IOException {
		ApiInfo api1 = loadVersion(a);
		ApiInfo api2 = loadVersion(b);
		Delta delta = new Delta(api1, api2);
		Path basedir = Paths.get("./target/diff").resolve(a.name()).resolve(b.name());
		new HTMLRenderer(new FileMultiReportOutput(basedir), b.getDoc()).render(delta);
	}

	public static void main(String[] args) throws IOException {
		createReport(JDK.V8, JDK.V9);
		createReport(JDK.V9, JDK.V10);
		createReport(JDK.V10, JDK.V11);
	}

}
