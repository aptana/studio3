/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package com.aptana.formatter.util;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * This exception class is used to dump large content to the stack trace field
 */
public class DumpContentException extends Exception
{

	private static final long serialVersionUID = 9087590677123278598L;

	public DumpContentException(String message)
	{
		super(message);
	}

	public void printStackTrace(PrintStream s)
	{
		synchronized (s)
		{
			s.println("========"); //$NON-NLS-1$
			s.println(getMessage());
			s.println("========"); //$NON-NLS-1$
		}
	}

	public void printStackTrace(PrintWriter s)
	{
		synchronized (s)
		{
			s.println("========"); //$NON-NLS-1$
			s.println(getMessage());
			s.println("========"); //$NON-NLS-1$
		}
	}
}
