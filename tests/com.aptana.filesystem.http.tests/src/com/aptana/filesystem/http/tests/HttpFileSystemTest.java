/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.filesystem.http.tests;

import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.TestCase;

import com.aptana.filesystem.http.HttpFileSystem;

public class HttpFileSystemTest extends TestCase
{
	public void testHttpFileSystem() throws URISyntaxException
	{
		HttpFileSystem fs = new HttpFileSystem();
		fs.getStore(new URI("http://www.google.com")); //$NON-NLS-1$
	}


}
