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

import java.io.IOException;
import java.io.OutputStream;

/**
 * Implementation of {@link IMultiReportOutput} that prefixes the output path.
 */
public class PrefixMultiReportOutput implements IMultiReportOutput {

	private IMultiReportOutput delegate;
	private String prefix;

	public PrefixMultiReportOutput(IMultiReportOutput delegate, String prefix) {
		this.delegate = delegate;
		this.prefix = prefix;
	}

	public OutputStream createFile(final String path) throws IOException {
		return delegate.createFile(prefix + path);
	}

}
