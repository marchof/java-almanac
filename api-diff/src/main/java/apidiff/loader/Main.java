package apidiff.loader;

import java.io.IOException;
import java.nio.file.Paths;

import apidiff.cmp.Delta;
import apidiff.model.ApiInfo;

public class Main {
	
	private static final ApiInfo loadZip(String path) throws IOException {
		ApiInfo api = new ApiInfo(path);
		Loader loader = new Loader(api, new PublicApiFilter());
		loader.loadZip(Paths.get(path));
		return api;	
	}
	
	private static final ApiInfo loadDirectory(String path) throws IOException {
		ApiInfo api = new ApiInfo(path);
		Loader loader = new Loader(api, new PublicApiFilter());
		loader.loadDirectory(Paths.get(path));
		return api;	
	}

	public static void main(String[] args) throws IOException {
		ApiInfo api1 = loadDirectory("/Library/Java/JavaVirtualMachines/jdk1.8.0_152.jdk/Contents/Home/jre/lib");
		// ApiInfo api1 = loadDirectory("/Users/marc/Downloads/java-api-diff/jdk-10/jmods");
		ApiInfo api2 = loadDirectory("/Users/marc/Downloads/java-api-diff/jdk-12/jmods");
	
		Delta delta = new Delta(api1, api2);
		delta.tree(System.out);
	}

}
