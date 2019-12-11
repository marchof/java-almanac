package apidiff;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import apidiff.loader.Loader;
import apidiff.loader.PublicApiFilter;
import apidiff.model.ApiInfo;

public class JDKLookup {
	
	private static final Path HOME = Paths.get(System.getProperty("user.home"));
	
	private static final List<Path> LOCATIONS = Arrays.asList( //
			HOME.resolve(".sdkman/candidates/java"), //
			HOME.resolve(".historic-jdks"));
	

	public static ApiInfo loadVersion(JDK jdk) throws IOException {
		ApiInfo api = new ApiInfo(jdk.getName(), jdk.getImpl());
		Loader loader = new Loader(api, new PublicApiFilter());
		for (Path p : LOCATIONS) {
			Path jdkpath = p.resolve(jdk.getImpl());
			if (Files.isDirectory(jdkpath)) {
				loader.loadJDK(jdkpath);
				return api;				
			}
		}
		throw new IOException("JDK not found: " + jdk.getImpl());
	}
	
}
