package apidiff;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import apidiff.cmp.Delta;
import apidiff.loader.Loader;
import apidiff.loader.PublicApiFilter;
import apidiff.model.ApiInfo;
import apidiff.report.FileMultiReportOutput;
import apidiff.report.HTMLRenderer;

public class Main {
	
	private static final Map<JDK, ApiInfo> cache = new HashMap<>();

	private static ApiInfo loadVersion(JDK jdk) throws IOException {
		ApiInfo api = new ApiInfo(jdk.getName(), jdk.getImpl());
		Loader loader = new Loader(api, new PublicApiFilter());
		loader.loadJDK(Paths.get(System.getProperty("user.home"), ".sdkman/candidates/java", jdk.getImpl()));
		return api;
	}
	
	private static ApiInfo getVersion(JDK jdk) throws IOException {
		ApiInfo api = cache.get(jdk);
		if (api == null) {
			api = loadVersion(jdk);
			cache.put(jdk, api);
		}
		return api;
	}

	private static void createReport(JDK a, JDK b) throws IOException {
		ApiInfo api1 = getVersion(a);
		ApiInfo api2 = getVersion(b);
		if (!api1.hasModules() || !api2.hasModules()) {
			api1 = api1.withoutModules();
			api2 = api2.withoutModules();
		}
		Delta delta = new Delta(api1, api2);
		Path basedir = Paths.get("./target/diff").resolve(a.name()).resolve(b.name());
		new HTMLRenderer(new FileMultiReportOutput(basedir), b.getDoc()).render(delta);
	}

	public static void main(String[] args) throws IOException {
		JDK[] jdks = JDK.values();
		for (int i = 0; i < jdks.length; i++) {
			for (int j = i + 1; j < jdks.length; j++) {
				createReport(jdks[i], jdks[j]);
			}
		}
	}

}
