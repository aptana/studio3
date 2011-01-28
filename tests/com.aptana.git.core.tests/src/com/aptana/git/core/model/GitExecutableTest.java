/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core.model;

import java.io.IOException;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.IPreferenceConstants;

@SuppressWarnings("nls")
public class GitExecutableTest extends TestCase
{
	// FIXME This certainly won't work on Windows!
	private static final String FAKE_GIT_1_5 = "fake_git_1.5.sh";
	private static final String FAKE_GIT_1_6 = "fake_git_1.6.sh";

	@Override
	protected void tearDown() throws Exception
	{
		GitExecutable.fgExecutable = null;
		IEclipsePreferences prefs = new InstanceScope().getNode(GitPlugin.getPluginId());
		prefs.remove(IPreferenceConstants.GIT_EXECUTABLE_PATH);
		prefs.flush();
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

		IEclipsePreferences prefs = new InstanceScope().getNode(GitPlugin.getPluginId());
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

		IEclipsePreferences prefs = new InstanceScope().getNode(GitPlugin.getPluginId());
		prefs.remove(IPreferenceConstants.GIT_EXECUTABLE_PATH);
		prefs.flush();

		testDetectsInStandardLocation();
	}
}
