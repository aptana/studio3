/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.filesystem.http.tests;

import static org.junit.Assert.assertNotNull;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.filesystem.IFileStore;
import org.junit.Test;

import com.aptana.filesystem.http.HttpFileSystem;

public class HttpFileSystemTest
{
	@Test
	public void testHttpFileSystem() throws URISyntaxException
	{
		HttpFileSystem fs = new HttpFileSystem();
		IFileStore store = fs.getStore(new URI("http://www.google.com")); //$NON-NLS-1$
		assertNotNull(store);
	}


}
