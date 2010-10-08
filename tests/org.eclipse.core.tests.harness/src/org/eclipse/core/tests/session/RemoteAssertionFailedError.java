/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.tests.session;

import java.io.PrintStream;
import java.io.PrintWriter;
import junit.framework.AssertionFailedError;

public class RemoteAssertionFailedError extends AssertionFailedError {
	/**
	 * All serializable objects should have a stable serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private Object stackText;
	private String message;

	public RemoteAssertionFailedError(String message, String stackText) {
		this.message = message;
		this.stackText = stackText;
	}

	public void printStackTrace(PrintWriter stream) {
		stream.print(stackText);
	}

	public void printStackTrace(PrintStream stream) {
		stream.print(stackText);
	}

	public String getMessage() {
		return message;
	}
}
