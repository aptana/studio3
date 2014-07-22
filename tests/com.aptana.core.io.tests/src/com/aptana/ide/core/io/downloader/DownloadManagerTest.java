/**
 * Aptana Studio
 * Copyright (c) 2014 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.core.io.downloader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.FileUtil;
import com.aptana.ide.core.io.CoreIOPlugin;

/**
 * @author Chris Williams <cwilliams@appcelerator.com>
 */
public class DownloadManagerTest
{
	private Mockery context;
	private DownloadManager dm;
	private List<ContentDownloadRequest> requests;
	private ContentDownloadRequest cdr;

	@Before
	public void setUp() throws Exception
	{
		context = new Mockery()
		{
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};
		cdr = context.mock(ContentDownloadRequest.class);
		requests = new ArrayList<ContentDownloadRequest>();
		dm = new DownloadManager()
		{
			@Override
			public synchronized void addDownload(ContentDownloadRequest request)
			{
				requests.add(request);
				super.addDownload(cdr);
			}
		};
	}

	@After
	public void tearDown() throws Exception
	{
		cdr = null;
		dm = null;
		context = null;
		requests = null;
	}

	@Test
	public void testAddingFileURICreatesFileDownloadRequest() throws CoreException, URISyntaxException
	{
		dm.addURI(new URI("file:/fake/path/to/file.txt"));
		assertEquals(1, requests.size());
		assertTrue(requests.get(0) instanceof FileDownloadRequest);
	}

	@Test
	public void testAddingNonFileURICreatesGenericContentDownloadRequest() throws CoreException, URISyntaxException
	{
		dm.addURI(new URI("http://example.com/fake/path/to/file.txt"));
		assertEquals(1, requests.size());
		assertTrue(requests.get(0) instanceof ContentDownloadRequest);
	}

	@Test
	public void tesAddURIsAndURLsStartThenGetDownloadedPaths() throws CoreException, URISyntaxException
	{
		dm.addURI(new URI("http://example.com"));

		final IPath downloadPath = Path.fromPortableString("/path/to/downloaded/file");
		context.checking(new Expectations()
		{
			{
				oneOf(cdr).execute(with(any(IProgressMonitor.class)));

				oneOf(cdr).getResult();
				will(returnValue(Status.OK_STATUS));

				oneOf(cdr).getDownloadLocation();
				will(returnValue(downloadPath));
			}
		});
		IStatus result = dm.start(new NullProgressMonitor());
		assertTrue(result.isOK());
		List<IPath> paths = dm.getContentsLocations();
		assertEquals(1, paths.size());
		assertEquals(downloadPath, paths.get(0));
		context.assertIsSatisfied();
	}

	// TODO Add tests for when user cancels in the middle of the process

	@Test
	public void testAddManyRequestsAndOneOfThemFailsToDownload() throws CoreException, URISyntaxException
	{
		dm.addURI(new URI("http://example.com/index.html"));
		dm.addURI(new URI("http://example.com/fake_file_123.txt"),
				FileUtil.getTempDirectory().append("fake_file_123.txt").toFile());
		dm.addURIs(CollectionsUtil.newList(new URI("http://example.com/index.shtml")));

		final IPath downloadPath1 = Path.fromPortableString("/path/to/downloaded/file1");
		final IPath downloadPath2 = Path.fromPortableString("/path/to/downloaded/file2");
		final IStatus errorStatus = new Status(IStatus.ERROR, CoreIOPlugin.PLUGIN_ID, "Failed to download");
		context.checking(new Expectations()
		{
			{
				exactly(3).of(cdr).execute(with(any(IProgressMonitor.class)));

				exactly(3).of(cdr).getResult();
				will(onConsecutiveCalls(returnValue(Status.OK_STATUS), returnValue(errorStatus),
						returnValue(Status.OK_STATUS)));

				exactly(2).of(cdr).getDownloadLocation();
				will(onConsecutiveCalls(returnValue(downloadPath1), returnValue(downloadPath2)));
			}
		});
		// One of the downloads will fail, so the returned status will not be OK
		IStatus result = dm.start(new NullProgressMonitor());
		assertFalse(result.isOK());
		// Delve into the multi status and make sure that the first and third request are OK, 2nd was not
		assertTrue(result.isMultiStatus());
		MultiStatus multi = (MultiStatus) result;
		IStatus[] children = multi.getChildren();
		assertEquals(3, children.length);
		assertTrue(children[0].isOK());
		assertFalse(children[1].isOK());
		assertTrue(children[2].isOK());

		// Only two actually downloaded, let's ensure we got the paths we expect
		List<IPath> paths = dm.getContentsLocations();
		assertEquals(2, paths.size());
		assertEquals(downloadPath1, paths.get(0));
		assertEquals(downloadPath2, paths.get(1));
		context.assertIsSatisfied();
	}

	@Test
	public void testAttemptingToAddNullURLAddsNoRequest() throws CoreException
	{
		dm.addURL(null);
		assertEquals(0, requests.size());
	}

	@Test
	public void testAddRequestObjectDirectly() throws CoreException, URISyntaxException
	{
		dm.addDownload(cdr);

		final IPath downloadPath1 = Path.fromPortableString("/path/to/downloaded/file1");
		context.checking(new Expectations()
		{
			{
				exactly(1).of(cdr).execute(with(any(IProgressMonitor.class)));

				exactly(1).of(cdr).getResult();
				will(returnValue(Status.OK_STATUS));

				exactly(1).of(cdr).getDownloadLocation();
				will(returnValue(downloadPath1));
			}
		});

		IStatus result = dm.start(new NullProgressMonitor());
		assertTrue(result.isOK());
		assertTrue(result.isMultiStatus());
		MultiStatus multi = (MultiStatus) result;
		IStatus[] children = multi.getChildren();
		assertEquals(1, children.length);
		assertTrue(children[0].isOK());

		List<IPath> paths = dm.getContentsLocations();
		assertEquals(1, paths.size());
		assertEquals(downloadPath1, paths.get(0));
		context.assertIsSatisfied();
	}
}
