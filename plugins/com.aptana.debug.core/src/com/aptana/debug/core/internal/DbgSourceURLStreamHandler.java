/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable staticFieldNamingConvention
// $codepro.audit.disable unnecessaryExceptions

package com.aptana.debug.core.internal;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * @author Max Stepanov
 */
public class DbgSourceURLStreamHandler extends URLStreamHandler {

	private static DbgSourceURLStreamHandler instance;

	/*
	 * (non-Javadoc)
	 * @see java.net.URLStreamHandler#openConnection(java.net.URL)
	 */
	protected URLConnection openConnection(URL u) throws IOException {
		return null;
	}

	public static DbgSourceURLStreamHandler getDefault() {
		if (instance == null) {
			instance = new DbgSourceURLStreamHandler();
		}
		return instance;
	}

}
