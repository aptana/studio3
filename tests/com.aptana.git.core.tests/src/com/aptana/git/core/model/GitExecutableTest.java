/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Version;

import com.aptana.core.util.FileUtil;
import com.aptana.core.util.ProcessStatus;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.IPreferenceConstants;

@SuppressWarnings("nls")
public class GitExecutableTest
{
	// FIXME This certainly won't work on Windows!
	private static final String FAKE_GIT_1_5 = "test_files/fake_git_1.5.sh";
	private static final String FAKE_GIT_1_6 = "test_files/fake_git_1.6.sh";

	private Mockery context;

	@Before
	public void setUp() throws Exception
	{
		context = new Mockery()
		{
			{
				setImposteriser(ClassImposteriser.INSTANCE);
				setThreadingPolicy(new Synchroniser());
			}
		};
	}

	@After
	public void tearDown() throws Exception
	{
		GitExecutable.fgExecutable = null;
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(GitPlugin.getPluginId());
		prefs.remove(IPreferenceConstants.GIT_EXECUTABLE_PATH);
		prefs.flush();
		context = null;
	}

	@Test
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
	@Test
	public void testUsesPrefLocationFirst() throws Throwable
	{
		URL url = makeURLForExecutableFile(new Path(FAKE_GIT_1_6));

		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(GitPlugin.getPluginId());
		prefs.put(IPreferenceConstants.GIT_EXECUTABLE_PATH, url.getPath());
		prefs.flush();

		GitExecutable executable = GitExecutable.instance();
		assertEquals(Path.fromOSString(url.getPath()), executable.path());
	}

	@Test
	public void testDetectsInStandardLocation() throws Throwable
	{
		GitExecutable executable = GitExecutable.instance();
		IPath expectedLocation = Path.fromOSString("/usr/bin/git");
		// IPath local = Path.fromOSString("/usr/local/bin/git");
		// if (local.toFile().exists())
		// {
		// expectedLocation = local;
		// }
		assertEquals(expectedLocation, executable.path());
	}

	// Test that it reacts to changes in pref location
	@Test
	public void testReactsToPrefLocationChanges() throws Throwable
	{
		testUsesPrefLocationFirst();

		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(GitPlugin.getPluginId());
		prefs.remove(IPreferenceConstants.GIT_EXECUTABLE_PATH);
		prefs.flush();

		testDetectsInStandardLocation();
	}

	@Test
	public void testCloneUsesProgressFlagOnOneDotSeven() throws Throwable
	{
		final String stdOutText = "stdout";
		final String stdErrText = "stderr";
		final int exitCode = 0;
		final Process process = context.mock(Process.class);
		context.checking(new Expectations()
		{
			{
				allowing(process).getOutputStream();

				oneOf(process).getInputStream();
				will(returnValue(new ByteArrayInputStream(stdOutText.getBytes())));

				oneOf(process).getErrorStream();
				will(returnValue(new ByteArrayInputStream(stdErrText.getBytes())));

				oneOf(process).waitFor();
				will(returnValue(exitCode));
			}
		});

		final String sourceURI = "git@github.com:aptana/studio3.git";
		final IPath dest = FileUtil.getTempDirectory().append("clone_dest");
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

	@Test
	public void testCloneUsesProperArgsForShallow() throws Throwable
	{
		final String stdOutText = "stdout";
		final String stdErrText = "stderr";
		final int exitCode = 0;
		final Process process = context.mock(Process.class);
		context.checking(new Expectations()
		{
			{
				allowing(process).getOutputStream();

				oneOf(process).getInputStream();
				will(returnValue(new ByteArrayInputStream(stdOutText.getBytes())));

				oneOf(process).getErrorStream();
				will(returnValue(new ByteArrayInputStream(stdErrText.getBytes())));

				oneOf(process).waitFor();
				will(returnValue(exitCode));
			}
		});

		final String sourceURI = "git@github.com:aptana/studio3.git";
		final IPath dest = FileUtil.getTempDirectory().append("clone_dest");
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

	@Test
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
				allowing(process).getOutputStream();

				oneOf(process).getInputStream();
				will(returnValue(new ByteArrayInputStream(stdOutText.getBytes())));

				oneOf(process).getErrorStream();
				will(returnValue(new ByteArrayInputStream(stdErrText.getBytes())));

				oneOf(process).waitFor();
				will(returnValue(exitCode));
			}
		});

		GitExecutable.CloneRunnable runnable = new GitExecutable.CloneRunnable(process, monitor)
		{
			@Override
			protected IProgressMonitor convertMonitor(IProgressMonitor monitor)
			{
				return monitor;
			}
		};
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

	@Test
	public void testAPSTUD4596() throws Throwable
	{
		URL url = makeURLForExecutableFile(new Path("test_files/apstud4596_git.sh"));

		IPath path = Path.fromOSString(url.getPath());
		assertTrue(GitExecutable.acceptBinary(path));
		GitExecutable.setPreferenceGitPath(path);
		GitExecutable exe = GitExecutable.instance();
		assertEquals(Version.parseVersion("1.7.7.5"), exe.version());
	}

	@Test
	public void testMsysgitVersionString() throws Throwable
	{
		URL url = makeURLForExecutableFile(new Path("test_files/msysgit.sh"));

		IPath path = Path.fromOSString(url.getPath());
		assertTrue(GitExecutable.acceptBinary(path));
		GitExecutable.setPreferenceGitPath(path);
		GitExecutable exe = GitExecutable.instance();
		assertEquals(Version.parseVersion("1.6.4.msysgit_0"), exe.version());
	}

	@Test
	public void testMsysgitVersionStringWithTooManySegments() throws Throwable
	{
		URL url = makeURLForExecutableFile(new Path("test_files/1.7.7.5.msysgit.0.sh"));

		IPath path = Path.fromOSString(url.getPath());
		assertTrue(GitExecutable.acceptBinary(path));
		GitExecutable.setPreferenceGitPath(path);
		GitExecutable exe = GitExecutable.instance();
		assertEquals(Version.parseVersion("1.7.7.5_msysgit_0"), exe.version());
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
