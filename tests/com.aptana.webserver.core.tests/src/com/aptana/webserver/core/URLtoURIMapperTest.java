/**
 * Aptana Studio
 * Copyright (c) 2014 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.webserver.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.net.URI;
import java.net.URL;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.core.util.FileUtil;

public class URLtoURIMapperTest
{

	private URLtoURIMapper mapper;

	@Before
	public void setUp() throws Exception
	{
		URL baseURL = new URL("http://localhost"); //$NON-NLS-1$
		URI documentRoot = FileUtil.getTempDirectory().toFile().toURI();
		mapper = new URLtoURIMapper(baseURL, documentRoot);
	}

	@After
	public void tearDown() throws Exception
	{
		mapper = null;
	}

	@Test
	public void testResolveURI()
	{
		// here we map URL on server to file on disk
		IFileStore fileStore = mapper.resolve(URI.create("http://localhost/path/to/file.html"));
		assertNotNull(fileStore);
		assertFalse(fileStore.fetchInfo().exists());
		assertEquals(FileUtil.getTempDirectory().append("path").append("to").append("file.html").toFile().toURI()
				.toString(), fileStore.toURI().toString());
	}

	@Test
	public void testResolveIFileStore() throws CoreException
	{
		// here we map a file on disk to the URL on server
		URI uri = FileUtil.getTempDirectory().append("path").append("to").append("file.html").toFile().toURI();
		URI actual = mapper.resolve(EFS.getStore(uri));
		assertNotNull(actual);
		assertEquals("http://localhost/path/to/file.html", actual.toString());
	}

}
