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

import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import apidiff.cmp.Delta;
import apidiff.model.ApiInfo;
import apidiff.report.FileMultiReportOutput;
import apidiff.report.IMultiReportOutput;
import apidiff.report.PrefixMultiReportOutput;
import apidiff.report.S3MultiReportOutput;
import apidiff.report.html.HTMLRenderer;

public class Main {

	private static final Map<JDK, ApiInfo> cache = new HashMap<>();

	private static ApiInfo getVersion(JDK jdk) throws IOException {
		ApiInfo api = cache.get(jdk);
		if (api == null) {
			api = JDKLookup.loadVersion(jdk);
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
		IMultiReportOutput prefixOutput = new PrefixMultiReportOutput(output,
				String.format("%s/%s/", a.name(), b.name()));
		new HTMLRenderer(prefixOutput, b.getDoc()).render(delta);
	}

	private static IMultiReportOutput createLocalFileOutput() {
		Path basedir = Paths.get("./target/diff");
		return new FileMultiReportOutput(basedir);
	}

	private static IMultiReportOutput createS3Output() {
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard() //
				.withRegion("us-east-1") //
				.withCredentials(new SystemPropertiesCredentialsProvider()) //
				.build();
		return new S3MultiReportOutput(s3Client, "download.eclipselab.org", "jdkdiff/");
	}

	public static void main(String[] args) throws IOException {
		IMultiReportOutput output;
		if (args.length > 0 && "s3".equals(args[0])) {
			output = createS3Output();
		} else {
			output = createLocalFileOutput();
		}
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
