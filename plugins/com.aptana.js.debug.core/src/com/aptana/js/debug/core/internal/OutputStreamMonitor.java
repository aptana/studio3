/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.internal;

import java.io.InputStream;

@SuppressWarnings("restriction")
public class OutputStreamMonitor extends org.eclipse.debug.internal.core.OutputStreamMonitor {

	public OutputStreamMonitor(InputStream stream, String encoding) {
		super(stream, encoding);
	}

	/*
	 * @see org.eclipse.debug.internal.core.OutputStreamMonitor#startMonitoring()
	 */
	protected void startMonitoring() {
		super.startMonitoring();
	}

}
