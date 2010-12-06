/**
 * Copyright (c) 2005-2010 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.formatter.ui.epl;

import org.eclipse.osgi.util.NLS;

public class UIEplMessages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.formatter.ui.epl.UIEplMessages"; //$NON-NLS-1$
	public static String ExceptionHandler_seeErrorLogMessage;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, UIEplMessages.class);
	}

	private UIEplMessages()
	{
	}
}
