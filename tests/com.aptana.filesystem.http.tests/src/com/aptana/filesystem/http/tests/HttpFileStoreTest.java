/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.filesystem.http.tests;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.TestCase;

import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;

import com.aptana.filesystem.http.HttpFileStore;
import com.aptana.filesystem.http.HttpFileSystem;

public class HttpFileStoreTest extends TestCase
{
	HttpFileSystem fs = null;
	HttpFileStore store = null;
	HttpFileStore storeMalformed = null;
	String page = "https://www.google.com/ads/"; //$NON-NLS-1$
	String pageMalformed = "http://www:google:com/ads/"; //$NON-NLS-1$

	@Override
	protected void setUp() throws Exception
	{
		fs = new HttpFileSystem();
		store = (HttpFileStore) fs.getStore(new URI(page));
		storeMalformed = (HttpFileStore) fs.getStore(new URI(pageMalformed));

		super.setUp();
	}

	public void testHttpFileStore() throws URISyntaxException
	{
		assertNotNull(store);
	}

	public void testChildNamesIntIProgressMonitor() throws CoreException
	{
		String[] children = store.childNames(0, null);
		assertEquals(0, children.length);
	}

	public void testFetchInfoIntIProgressMonitor() throws CoreException
	{
		IFileInfo info = store.fetchInfo(0, null);
		assertTrue(info.exists());
	}

	public void testGetChildString() throws CoreException
	{
		IFileStore childStore = store.getChild("searchads"); //$NON-NLS-1$
		IFileInfo info = childStore.fetchInfo(0, null);
		assertTrue(info.exists());
	}

	public void testGetName()
	{
		assertEquals(page, store.getName());
	}

	public void testGetParent() throws URISyntaxException
	{
		assertEquals(new URI("https://www.google.com/"), store.getParent().toURI()); //$NON-NLS-1$
	}

	public void testToURI() throws URISyntaxException
	{
		assertEquals(new URI(page), store.toURI());
	}

	public void testToLocalFileIntIProgressMonitor() throws CoreException
	{
		File file = store.toLocalFile(0, null);
		assertTrue(file.exists());
		file = store.toLocalFile(0, null);
		assertTrue(file.exists());

		file.delete();
		assertFalse(file.exists());

		// get it again, testing APSTUD-4725
		file = store.toLocalFile(0, null);
		assertTrue(file.exists());
		file.delete();
		assertFalse(file.exists());

	}

	public void testOpenInputStreamIntIProgressMonitor() throws CoreException
	{
		InputStream stream = store.openInputStream(0, null);
		assertNotNull(stream);

		try
		{
			stream = storeMalformed.openInputStream(0, null);
			fail();
		}
		catch (CoreException ex)
		{
		}

	}

}
