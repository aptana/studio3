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
package com.aptana.formatter.epl;

import org.eclipse.osgi.util.NLS;

public class FormatterMessages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.formatter.epl.FormatterMessages"; //$NON-NLS-1$
	public static String ExceptionHandler_seeErrorLogMessage;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, FormatterMessages.class);
	}

	private FormatterMessages()
	{
	}
}
