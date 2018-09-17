package apidiff.loader;

import java.io.IOException;
import java.nio.file.Paths;

import apidiff.cmp.Delta;
import apidiff.model.ApiInfo;

public class Main {
	
	private static final ApiInfo loadVersion(String version) throws IOException {
		ApiInfo api = new ApiInfo(version);
		Loader loader = new Loader(api, new PublicApiFilter());
		loader.loadJDK(Paths.get(System.getProperty("user.home"), ".sdkman/candidates/java", version));
		return api;	
	}

	public static void main(String[] args) throws IOException {
		
		 ApiInfo api1 = loadVersion("8.0.181-oracle");
	    //ApiInfo api1 = loadDirectory("9.0.4-open");
		// ApiInfo api1 = loadDirectory("10.0.2-open");
		// ApiInfo api1 = loadDirectory("11.ea.28-open");
		ApiInfo api2 = loadVersion("12.ea.08-open");
	
		Delta delta = new Delta(api1, api2);
		delta.tree(System.out);
	}

}
