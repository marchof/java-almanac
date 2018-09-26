/*******************************************************************************
 * Copyright (c) 2009, 2018 Mountainminds GmbH & Co. KG and Contributors
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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Implementation of {@link IMultiReportOutput} that writes files directly to a
 * given directory.
 */
public class FileMultiReportOutput implements IMultiReportOutput {

	private final Path basedir;

	/**
	 * Creates a new instance for document output in the given base directory.
	 * 
	 * @param basedir
	 *            base directory
	 */
	public FileMultiReportOutput(final Path basedir) {
		this.basedir = basedir;
	}

	public OutputStream createFile(final String path) throws IOException {
		Path file = basedir.resolve(path);
		Files.createDirectories(file.getParent());
		return new BufferedOutputStream(Files.newOutputStream(file));
	}

	public void close() throws IOException {
		// nothing to do here
	}

}
