/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core.model;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.osgi.framework.Version;

import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.ProcessStatus;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.IPreferenceConstants;

@SuppressWarnings("nls")
public class GitExecutableTest extends TestCase
{
	// FIXME This certainly won't work on Windows!
	private static final String FAKE_GIT_1_5 = "fake_git_1.5.sh";
	private static final String FAKE_GIT_1_6 = "fake_git_1.6.sh";

	private Mockery context;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		context = new Mockery()
		{
			{
				setImposteriser(ClassImposteriser.INSTANCE);
			}
		};
	}

	@Override
	protected void tearDown() throws Exception
	{
		GitExecutable.fgExecutable = null;
		IEclipsePreferences prefs = EclipseUtil.instanceScope().getNode(GitPlugin.getPluginId());
		prefs.remove(IPreferenceConstants.GIT_EXECUTABLE_PATH);
		prefs.flush();
		context = null;
		super.tearDown();
	}

	public void testAcceptBinary() throws Exception
	{
		URL url = makeURLForExecutableFile(new Path(FAKE_GIT_1_5));

		assertFalse(GitExecutable.acceptBinary(Path.fromOSString(url.getPath())));

		url = makeURLForExecutableFile(new Path(FAKE_GIT_1_6));
		assertTrue(GitExecutable.acceptBinary(Path.fromOSString(url.getPath())));
	}

	protected URL makeURLForExecutableFile(IPath path) throws IOException
	{
		URL url = FileLocator.find(GitPlugin.getDefault().getBundle(), path, null);
		url = FileLocator.toFileURL(url);
		if (!Platform.getOS().equals(Platform.OS_WIN32))
		{
			try
			{
				IFileStore fileStore = EFS.getStore(url.toURI());
				IFileInfo fileInfo = fileStore.fetchInfo();
				if (!fileInfo.getAttribute(EFS.ATTRIBUTE_EXECUTABLE))
				{
					fileInfo.setAttribute(EFS.ATTRIBUTE_EXECUTABLE, true);
					fileStore.putInfo(fileInfo, EFS.SET_ATTRIBUTES, null);
				}
			}
			catch (Exception e)
			{
			}
		}
		return url;
	}

	// Test that it picks up pref value for location above all else
	public void testUsesPrefLocationFirst() throws Throwable
	{
		URL url = makeURLForExecutableFile(new Path(FAKE_GIT_1_6));

		IEclipsePreferences prefs = EclipseUtil.instanceScope().getNode(GitPlugin.getPluginId());
		prefs.put(IPreferenceConstants.GIT_EXECUTABLE_PATH, url.getPath());
		prefs.flush();

		GitExecutable executable = GitExecutable.instance();
		assertEquals(Path.fromOSString(url.getPath()), executable.path());
	}

	public void testDetectsInStandardLocation() throws Throwable
	{
		GitExecutable executable = GitExecutable.instance();
		// FIXME This is hacky for test, but basically on my machine I have it in /usr.local.bin.git, while test box has
		// /usr/bin/git
		IPath expectedLocation = Path.fromOSString("/usr/bin/git");
		IPath local = Path.fromOSString("/usr/local/bin/git");
		if (local.toFile().exists())
		{
			expectedLocation = local;
		}
		assertEquals(expectedLocation, executable.path());
	}

	// Test that it reacts to changes in pref location
	public void testReactsToPrefLocationChanges() throws Throwable
	{
		testUsesPrefLocationFirst();

		IEclipsePreferences prefs = EclipseUtil.instanceScope().getNode(GitPlugin.getPluginId());
		prefs.remove(IPreferenceConstants.GIT_EXECUTABLE_PATH);
		prefs.flush();

		testDetectsInStandardLocation();
	}

	public void testCloneUsesProgressFlagOnOneDotSeven() throws Throwable
	{
		final String stdOutText = "stdout";
		final String stdErrText = "stderr";
		final int exitCode = 0;
		final Process process = context.mock(Process.class);
		context.checking(new Expectations()
		{
			{
				oneOf(process).getInputStream();
				will(returnValue(new ByteArrayInputStream(stdOutText.getBytes())));

				oneOf(process).getErrorStream();
				will(returnValue(new ByteArrayInputStream(stdErrText.getBytes())));

				oneOf(process).waitFor();
				will(returnValue(exitCode));
			}
		});

		final String sourceURI = "git@github.com:aptana/studio3.git";
		final IPath dest = Path.fromOSString(new File(File.createTempFile("clone_dest", "tmp").getParent(),
				"clone_dest").getAbsolutePath());
		IPath gitPath = Path.fromPortableString("/fake/git/path");
		GitExecutable executable = new GitExecutable(gitPath)
		{
			@Override
			protected Process run(Map<String, String> env, String... args) throws IOException, CoreException
			{
				// Assert the args are what we expect
				assertEquals("Wrong number of arguments to git clone invocation", 5, args.length);
				assertEquals("clone", args[0]);
				assertEquals("--progress", args[1]);
				assertEquals("--", args[2]);
				assertEquals(sourceURI, args[3]);
				assertEquals(dest.toOSString(), args[4]);
				return process;
			}

			@Override
			public Version version()
			{
				return new Version("1.7.0");
			}
		};

		IStatus status = executable.clone(sourceURI, dest, false, new NullProgressMonitor());
		assertNotNull(status);
		assertTrue(status instanceof ProcessStatus);
		ProcessStatus pStatus = (ProcessStatus) status;
		assertEquals(exitCode, pStatus.getCode());
		assertEquals(stdOutText, pStatus.getStdOut());
		assertEquals(stdErrText, pStatus.getStdErr());
		context.assertIsSatisfied();
	}

	public void testCloneUsesProperArgsForShallow() throws Throwable
	{
		final String stdOutText = "stdout";
		final String stdErrText = "stderr";
		final int exitCode = 0;
		final Process process = context.mock(Process.class);
		context.checking(new Expectations()
		{
			{
				oneOf(process).getInputStream();
				will(returnValue(new ByteArrayInputStream(stdOutText.getBytes())));

				oneOf(process).getErrorStream();
				will(returnValue(new ByteArrayInputStream(stdErrText.getBytes())));

				oneOf(process).waitFor();
				will(returnValue(exitCode));
			}
		});

		final String sourceURI = "git@github.com:aptana/studio3.git";
		final IPath dest = Path.fromOSString(new File(File.createTempFile("clone_dest", "tmp").getParent(),
				"clone_dest").getAbsolutePath());
		IPath gitPath = Path.fromPortableString("/fake/git/path");
		GitExecutable executable = new GitExecutable(gitPath)
		{
			@Override
			protected Process run(Map<String, String> env, String... args) throws IOException, CoreException
			{
				// Assert the args are what we expect
				assertEquals("Wrong number of arguments to git clone invocation", 6, args.length);
				assertEquals("clone", args[0]);
				assertEquals("--depth", args[1]);
				assertEquals("1", args[2]);
				assertEquals("--", args[3]);
				assertEquals(sourceURI, args[4]);
				assertEquals(dest.toOSString(), args[5]);
				return process;
			}

			@Override
			public Version version()
			{
				return new Version("1.6.0");
			}
		};

		IStatus status = executable.clone(sourceURI, dest, true, new NullProgressMonitor());
		assertNotNull(status);
		assertTrue(status instanceof ProcessStatus);
		ProcessStatus pStatus = (ProcessStatus) status;
		assertEquals(exitCode, pStatus.getCode());
		assertEquals(stdOutText, pStatus.getStdOut());
		assertEquals(stdErrText, pStatus.getStdErr());
		context.assertIsSatisfied();
	}

	public void testCloneRunnableProvidesProgress() throws Throwable
	{
		TestProgressMonitor monitor = new TestProgressMonitor();
		final String stdOutText = "stdout";
		final String stdErrText = "Receiving objects:   2% (1/39)   \nReceiving objects:   5% (2/39)   \nReceiving objects:   7% (3/39)   ";
		final int exitCode = 0;
		final Process process = context.mock(Process.class);
		context.checking(new Expectations()
		{
			{
				oneOf(process).getInputStream();
				will(returnValue(new ByteArrayInputStream(stdOutText.getBytes())));

				oneOf(process).getErrorStream();
				will(returnValue(new ByteArrayInputStream(stdErrText.getBytes())));

				oneOf(process).waitFor();
				will(returnValue(exitCode));
			}
		});

		GitExecutable.CloneRunnable runnable = new GitExecutable.CloneRunnable(process, monitor);
		Thread t = new Thread(runnable);
		t.start();
		t.join();

		IStatus status = runnable.getResult();
		assertNotNull(status);
		assertTrue(status instanceof ProcessStatus);
		ProcessStatus pStatus = (ProcessStatus) status;
		assertEquals(exitCode, pStatus.getCode());
		assertEquals(stdOutText, pStatus.getStdOut());
		assertEquals(stdErrText, pStatus.getStdErr());
		// verify that we picked up the last percentage correctly
		assertEquals("Monitor isn't reporting proper work units based on stderr output from clone", 7,
				monitor.getWorkedUnits());
		context.assertIsSatisfied();
	}

	private class TestProgressMonitor extends NullProgressMonitor
	{

		private int work_total = 0;

		@Override
		public void worked(int work)
		{
			work_total += work;
			super.worked(work);
		}

		@Override
		public void done()
		{
			// ignore
		}

		int getWorkedUnits()
		{
			return work_total;
		}
	}
}
