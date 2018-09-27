package apidiff;

import java.io.IOException;
import java.nio.file.Paths;

import apidiff.cmp.Delta;
import apidiff.javadoc.IJavaDocLinkProvider;
import apidiff.javadoc.JavaDoc11;
import apidiff.loader.Loader;
import apidiff.loader.PublicApiFilter;
import apidiff.model.ApiInfo;
import apidiff.report.FileMultiReportOutput;
import apidiff.report.HTMLRenderer;

public class Main {
	
	private static final ApiInfo loadVersion(String version) throws IOException {
		ApiInfo api = new ApiInfo(version);
		Loader loader = new Loader(api, new PublicApiFilter());
		loader.loadJDK(Paths.get(System.getProperty("user.home"), ".sdkman/candidates/java", version));
		return api;	
	}

	public static void main(String[] args) throws IOException {
		
		// ApiInfo api1 = loadVersion("8.0.181-oracle");
	    // ApiInfo api1 = loadVersion("9.0.4-open");
		 ApiInfo api1 = loadVersion("10.0.2-open");
		 ApiInfo api2 = loadVersion("11.0.0-open");
		// ApiInfo api2 = loadVersion("12.ea.08-open");
	
		IJavaDocLinkProvider doc = new JavaDoc11("https://docs.oracle.com/en/java/javase/11/docs/api/");
		 
		Delta delta = new Delta(api1, api2);
		
		new HTMLRenderer(new FileMultiReportOutput(Paths.get("./target/diff")), doc).render(delta);
		delta.tree(System.out);
	}

}
