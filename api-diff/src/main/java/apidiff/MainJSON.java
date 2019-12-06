/*******************************************************************************
 * Copyright (c) 2009, 2019 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 *******************************************************************************/
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
import apidiff.report.IMultiReportOutput;
import apidiff.report.json.JSONRenderer;

public class MainJSON {

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

	private static void createReport(JDK a, JDK b, IMultiReportOutput output) throws IOException {
		ApiInfo api1 = getVersion(a);
		ApiInfo api2 = getVersion(b);
		if (!api1.hasModules() || !api2.hasModules()) {
			api1 = api1.withoutModules();
			api2 = api2.withoutModules();
		}
		Delta delta = new Delta(api1, api2);
		new JSONRenderer(output, b.getDoc()).render(delta);
	}

	private static IMultiReportOutput createLocalFileOutput() {
		Path basedir = Paths.get("../site");
		return new FileMultiReportOutput(basedir);
	}

	public static void main(String[] args) throws IOException {
		IMultiReportOutput output = createLocalFileOutput();
		JDK[] jdks = JDK.values();
		for (int i = 0; i < jdks.length; i++) {
			for (int j = i + 1; j < jdks.length; j++) {
				JDK a = jdks[i];
				JDK b = jdks[j];
				System.out.printf("Creating %s -> %s%n", a, b);
				createReport(a, b, output);
			}
		}
	}

}
