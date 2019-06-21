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
package apidiff.report;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

/**
 * Implementation of {@link IMultiReportOutput} that writes files directly to a
 * S3 bucket and makes them public readable.
 */
public class S3MultiReportOutput implements IMultiReportOutput {

	private AmazonS3 s3Client;

	private String bucketName;

	private String basePath;

	/**
	 * Creates a new instance for document output to the given S3 client.
	 */
	public S3MultiReportOutput(AmazonS3 s3Client, String bucketName, String basePath) {
		this.s3Client = s3Client;
		this.bucketName = bucketName;
		this.basePath = basePath;
	}

	public OutputStream createFile(final String path) throws IOException {
		return new ByteArrayOutputStream() {
			@Override
			public void close() throws IOException {
				upload(path, toByteArray());
			}
		};
	}

	private void upload(String path, byte[] content) {
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(content.length);
		metadata.setContentType(getContentType(path));
		PutObjectRequest request = new PutObjectRequest(bucketName, basePath + path, new ByteArrayInputStream(content),
				metadata).withCannedAcl(CannedAccessControlList.PublicRead);
		s3Client.putObject(request);
	}

	private String getContentType(String path) {
		if (path.endsWith(".css")) {
			return "text/css";
		}
		if (path.endsWith(".html")) {
			return "text/html";
		}
		if (path.endsWith(".png")) {
			return "image/png";
		}
		return "";
	}

	public void close() throws IOException {
		// nothing to do here
	}

}
