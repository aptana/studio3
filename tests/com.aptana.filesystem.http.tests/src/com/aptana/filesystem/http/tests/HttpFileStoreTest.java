/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
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

import org.eclipse.core.filesystem.EFS;
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
	String pageParent = "https://www.google.com/"; //$NON-NLS-1$

	@Override
	protected void setUp() throws Exception
	{
		fs = new HttpFileSystem();
		store = getStoreByString(page);

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception
	{
		fs = null;
		store = null;
		storeMalformed = null;

		super.tearDown();
	}

	public void testHttpFileStore() throws URISyntaxException
	{
		assertNotNull(store);
	}

	public void testChildNamesIntIProgressMonitor() throws CoreException
	{
		String[] children = store.childNames(EFS.CACHE, null);
		assertEquals(0, children.length);
	}

	public void testFetchInfoIntIProgressMonitor() throws CoreException
	{
		IFileInfo info = store.fetchInfo(EFS.CACHE, null);
		assertTrue(info.exists());
	}

	public void testGetChildString() throws CoreException
	{
		IFileStore childStore = store.getChild("searchads"); //$NON-NLS-1$
		IFileInfo info = childStore.fetchInfo(EFS.CACHE, null);
		assertTrue(info.exists());
	}

	public void testGetName()
	{
		assertEquals(page, store.getName());
	}

	public void testGetParent() throws URISyntaxException
	{
		assertEquals(new URI(pageParent), store.getParent().toURI());
	}

	public void testToURI() throws URISyntaxException
	{
		assertEquals(new URI(page), store.toURI());
	}

	public void testToLocalFileNoCache() throws CoreException
	{
		// will create a file at this location
		IFileStore parentStore = store.getParent();
		File parentFile = parentStore.toLocalFile(EFS.NONE, null);
		assertNull(parentFile);
	}

	public void testToLocalFile() throws CoreException
	{
		File file = store.toLocalFile(EFS.CACHE, null);
		assertTrue(file.exists());
		assertTrue(file.isFile());
	}

	public void testToLocalFileDownloadTwice() throws CoreException
	{
		File file = store.toLocalFile(EFS.CACHE, null);
		assertTrue(file.exists());
		assertTrue(file.isFile());

		file.delete();
		assertFalse(file.exists());

		// get it again, testing APSTUD-4725
		file = store.toLocalFile(EFS.CACHE, null);
		assertTrue(file.exists());
	}

	public void testToLocalFileGetChildThenParent() throws CoreException
	{
		// will get the parent file (i.e. www.google.com)
		IFileStore parentStore = store.getParent();
		File parentFile = parentStore.toLocalFile(EFS.CACHE, null);
		assertTrue(parentFile.exists());
		assertTrue(parentFile.isFile());

		// will now try and gt the child file (i.e. www.google.com/ads)
		File file = store.toLocalFile(EFS.CACHE, null);
		assertTrue(file.exists());
		assertTrue(file.isFile());
		file = store.toLocalFile(EFS.CACHE, null);
		assertTrue(file.exists());

		file.delete();
		assertFalse(file.exists());

		// now make sure we can have a parent file again
		parentFile = parentStore.toLocalFile(EFS.CACHE, null);
		assertTrue(parentFile.exists());
		assertTrue(parentFile.isFile());
	}

	public void testGetPathNoProtocol() throws CoreException, URISyntaxException
	{
		String path1 = HttpFileStore.getPath(new URI("www.google.com")); //$NON-NLS-1$
		String path2 = HttpFileStore.getPath(new URI("http://www.google.com/")); //$NON-NLS-1$

		assertNotSame(path1, path2);
	}

	public void testGetPathTrailingSlash() throws CoreException, URISyntaxException
	{
		String path1 = HttpFileStore.getPath(new URI("http://www.google.com")); //$NON-NLS-1$
		String path2 = HttpFileStore.getPath(new URI("http://www.google.com/")); //$NON-NLS-1$

		assertNotSame(path1, path2);
	}

	public void testGetPathIndexFile() throws CoreException, URISyntaxException
	{
		String path1 = HttpFileStore.getPath(new URI("http://www.google.com/")); //$NON-NLS-1$
		String path2 = HttpFileStore.getPath(new URI("http://www.google.com/index.html")); //$NON-NLS-1$

		assertNotSame(path1, path2);
	}

	public void testGetPathPorts() throws CoreException, URISyntaxException
	{
		String path1 = HttpFileStore.getPath(new URI("http://www.google.com/")); //$NON-NLS-1$
		String path2 = HttpFileStore.getPath(new URI("https://www.google.com/")); //$NON-NLS-1$

		assertNotSame(path1, path2);
	}

	public void testGetPathPorts2() throws CoreException, URISyntaxException
	{
		String path1 = HttpFileStore.getPath(new URI("http://www.google.com:80/")); //$NON-NLS-1$
		String path2 = HttpFileStore.getPath(new URI("https://www.google.com/")); //$NON-NLS-1$

		assertNotSame(path1, path2);
	}

	public void testGetPathCaseSensitive() throws CoreException, URISyntaxException
	{
		String path1 = HttpFileStore.getPath(new URI("http://www.google.com/")); //$NON-NLS-1$
		String path2 = HttpFileStore.getPath(new URI("http://WWW.GOOGLE.COM/")); //$NON-NLS-1$

		assertNotSame(path1, path2);
	}

	public void testGetPathMultilingual() throws CoreException, URISyntaxException
	{
		String path1 = HttpFileStore.getPath(new URI("스타벅스코리아.com")); //$NON-NLS-1$ (Starbucks Korea)
		assertNotNull(path1);
	}

	public void testToLocalFileSimilarUrls() throws CoreException, URISyntaxException
	{
		HttpFileStore store1 = getStoreByString("http://www.google.com"); //$NON-NLS-1$
		HttpFileStore store2 = getStoreByString("http://www.google.com/"); //$NON-NLS-1$
		HttpFileStore store3 = getStoreByString("https://www.google.com/"); //$NON-NLS-1$
		HttpFileStore store4 = getStoreByString("https://www.google.com/"); //$NON-NLS-1$

		File file1 = store1.toLocalFile(EFS.CACHE, null);
		assertTrue(file1.exists());
		assertTrue(file1.isFile());

		File file2 = store2.toLocalFile(EFS.CACHE, null);
		assertTrue(file2.exists());
		assertTrue(file2.isFile());

		File file3 = store3.toLocalFile(EFS.CACHE, null);
		assertTrue(file3.exists());
		assertTrue(file3.isFile());

		File file4 = store4.toLocalFile(EFS.CACHE, null);
		assertTrue(file4.exists());
		assertTrue(file4.isFile());

		assertNotSame(file1, file2);
		assertNotSame(file1, file3);
		assertNotSame(file1, file4);
		assertNotSame(file2, file3);
		assertNotSame(file2, file4);
		assertNotSame(file3, file4);
	}

	public void testOpenInputStreamIntIProgressMonitor() throws CoreException
	{
		InputStream stream = store.openInputStream(0, null);
		assertNotNull(stream);

	}

	public void testOpenInputStreamMalformedIntIProgressMonitor() throws CoreException, URISyntaxException
	{
		try
		{
			HttpFileStore storeMalformed = getStoreByString("http://www:google:com/ads/"); //$NON-NLS-1$
			storeMalformed.openInputStream(0, null);
			fail();
		}
		catch (CoreException ex)
		{
		}

	}

	private HttpFileStore getStoreByString(String uri) throws URISyntaxException
	{
		return (HttpFileStore) fs.getStore(new URI(uri));
	}

}
