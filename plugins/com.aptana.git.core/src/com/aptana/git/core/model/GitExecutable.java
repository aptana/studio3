/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core.model;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.ShellExecutable;
import com.aptana.core.util.ExecutableUtil;
import com.aptana.core.util.PlatformUtil;
import com.aptana.core.util.ProcessUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.IPreferenceConstants;

public class GitExecutable
{
	private static final String GIT_EXECUTABLE = "git"; //$NON-NLS-1$
	protected static final String GIT_EXECUTABLE_WIN32 = GIT_EXECUTABLE + ".exe"; //$NON-NLS-1$
	public static final String MIN_GIT_VERSION = "1.6.0"; //$NON-NLS-1$
	private static ArrayList<IPath> fgLocations;
	private IPath gitPath;

	static GitExecutable fgExecutable;
	private static boolean fgAddedPrefListener;

	private GitExecutable(IPath gitPath)
	{
		this.gitPath = gitPath;
	}

	public synchronized static GitExecutable instance()
	{
		// FIXME Singletons are bad! hide behind an interface and grab the global instance of this from the plugin?
		if (fgExecutable == null)
		{
			fgExecutable = GitExecutable.find();
			if (!fgAddedPrefListener)
			{
				new InstanceScope().getNode(GitPlugin.getPluginId()).addPreferenceChangeListener(
						new IEclipsePreferences.IPreferenceChangeListener()
						{

							public void preferenceChange(PreferenceChangeEvent event)
							{
								if (!event.getKey().equals(IPreferenceConstants.GIT_EXECUTABLE_PATH))
									return;
								fgExecutable = null;
								// reset shell path preferences on Win32
								if (Platform.OS_WIN32.equals(Platform.getOS()))
								{
									ShellExecutable.setPreferenceShellPath(null);
								}
							}
						});
				fgAddedPrefListener = true;
			}
		}
		return fgExecutable;
	}

	private static IPath getPreferenceGitPath()
	{
		String pref = new InstanceScope().getNode(GitPlugin.PLUGIN_ID).get(IPreferenceConstants.GIT_EXECUTABLE_PATH,
				null);
		if (!StringUtil.isEmpty(pref))
		{
			IPath path = Path.fromOSString(pref);
			if (path.toFile().isDirectory())
			{
				boolean isWin32 = Platform.OS_WIN32.equals(Platform.getOS());
				path = path.append(isWin32 ? GIT_EXECUTABLE_WIN32 : GIT_EXECUTABLE);
			}
			if (acceptBinary(path))
			{
				return path;
			}
			GitPlugin
					.logError(
							MessageFormat
									.format("You entered a custom git path in the Preferences pane, but this path is not a valid git v{0} or higher binary. We're going to use the default search paths instead", //$NON-NLS-1$
											MIN_GIT_VERSION), null);
		}
		return null;
	}

	public static void setPreferenceGitPath(IPath path)
	{
		IEclipsePreferences prefs = new InstanceScope().getNode(GitPlugin.PLUGIN_ID);
		if (path != null)
		{
			prefs.put(IPreferenceConstants.GIT_EXECUTABLE_PATH, path.toOSString());
		}
		else
		{
			prefs.remove(IPreferenceConstants.GIT_EXECUTABLE_PATH);
		}
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			GitPlugin.logError("Saving preferences failed.", e); //$NON-NLS-1$
		}
		fgExecutable = null;
		if (Platform.OS_WIN32.equals(Platform.getOS()))
		{
			if (path != null && path.toFile().isFile())
			{
				path = path.removeLastSegments(1);
			}
			ShellExecutable.setPreferenceShellPath(path);
		}
	}

	private static GitExecutable find()
	{
		IPath prefPath = getPreferenceGitPath();
		if (prefPath != null)
		{
			PortableGit.checkInstallation(prefPath);
			return new GitExecutable(prefPath);
		}

		boolean isWin32 = Platform.OS_WIN32.equals(Platform.getOS());
		IPath path = ExecutableUtil.find(isWin32 ? GIT_EXECUTABLE_WIN32 : GIT_EXECUTABLE, false, searchLocations(),
				new FileFilter()
				{

					public boolean accept(File pathname)
					{
						return acceptBinary(Path.fromOSString(pathname.getAbsolutePath()));
					}
				});
		if (path != null)
		{
			return new GitExecutable(path);
		}

		path = PortableGit.getLocation();
		if (path != null)
		{
			setPreferenceGitPath(path);
			return new GitExecutable(path);
		}

		log(MessageFormat.format("Could not find a git binary higher than version {0}", MIN_GIT_VERSION)); //$NON-NLS-1$
		return null;
	}

	private static void log(String string)
	{
		GitPlugin.logInfo(string);
	}

	private synchronized static List<IPath> searchLocations()
	{
		if (fgLocations == null)
		{
			fgLocations = new ArrayList<IPath>();
			if (Platform.getOS().equals(Platform.OS_WIN32))
			{
				fgLocations.add(Path.fromOSString(PlatformUtil.expandEnvironmentStrings("%PROGRAMW6432%\\Git\\bin"))); //$NON-NLS-1$
				fgLocations.add(Path.fromOSString(PlatformUtil.expandEnvironmentStrings("%PROGRAMFILES%\\Git\\bin"))); //$NON-NLS-1$
				fgLocations.add(Path.fromOSString(PlatformUtil
						.expandEnvironmentStrings("%PROGRAMFILES(X86)%\\Git\\bin"))); //$NON-NLS-1$
				fgLocations.add(Path.fromOSString("C:\\RailsInstaller\\Git\\bin")); //$NON-NLS-1$
			}
			else
			{
				fgLocations.add(Path.fromOSString("/opt/local/bin")); //$NON-NLS-1$
				fgLocations.add(Path.fromOSString("/sw/bin")); //$NON-NLS-1$
				fgLocations.add(Path.fromOSString("/opt/git/bin")); //$NON-NLS-1$
				fgLocations.add(Path.fromOSString("/usr/local/bin")); //$NON-NLS-1$
				fgLocations.add(Path.fromOSString("/usr/local/git/bin")); //$NON-NLS-1$
				fgLocations.add(Path.fromOSString(PlatformUtil.expandEnvironmentStrings("~/bin"))); //$NON-NLS-1$
			}
		}
		return fgLocations;
	}

	private static String versionForPath(IPath path)
	{
		if (path == null)
			return null;

		if (!path.toFile().isFile())
			return null;

		String version = ProcessUtil.outputForCommand(path.toOSString(), null, "--version"); //$NON-NLS-1$
		if (version != null && version.startsWith("git version ")) //$NON-NLS-1$
			return version.substring(12);

		return null;
	}

	public static boolean acceptBinary(IPath path)
	{
		if (path == null)
			return false;

		String version = versionForPath(path);
		if (version == null)
			return false;

		int c = version.compareTo(MIN_GIT_VERSION);
		if (c >= 0)
		{
			return true;
		}

		log(MessageFormat.format("Found a git binary at {0}, but is only version {1}", path, version)); //$NON-NLS-1$
		return false;
	}

	public IPath path()
	{
		return gitPath;
	}

	/**
	 * Launches the git process and returns the result of the operation in an IStatus. Please DO NOT USE THIS METHOD if
	 * you can get the operation done through GitRepository (or it should live there)! Otherwise we cannot properly
	 * maintain a lock/monitor on reads and writes to avoid git processes stomping on each other!
	 * 
	 * @param workingDir
	 * @param args
	 * @return
	 */
	public IStatus runInBackground(IPath workingDir, String... args)
	{
		return ProcessUtil.runInBackground(gitPath.toOSString(), workingDir, args);
	}

	IStatus runInBackground(IPath workingDir, Map<String, String> env, String... args)
	{
		// FIXME Inline into GitRepository.execute?
		return ProcessUtil.runInBackground(gitPath.toOSString(), workingDir, env, args);
	}

	/**
	 * Launches the git process and returns a map from the exit value to the stdout output read in.
	 * 
	 * @param workingDirectory
	 * @param input
	 * @param args
	 * @return
	 */
	IStatus runInBackground(String input, IPath workingDirectory, String... args)
	{
		// FIXME Inline into GitRepository.executeWithInput?
		return ProcessUtil.runInBackground(gitPath.toOSString(), workingDirectory, input, null, args);
	}

	/**
	 * Launches the git process and returns the handle to the active process.
	 * 
	 * @param directory
	 * @param arguments
	 * @return
	 * @throws IOException
	 * @throws CoreException
	 */
	Process run(IPath directory, String... arguments) throws IOException, CoreException
	{
		// FIXME Inline into GitRevList.walkRevisionListWithSpecifier
		return ProcessUtil.run(gitPath.toOSString(), directory, arguments);
	}

	/**
	 * Sets up the environment map in a way that our special GIT_SSH env value is set so that the SSH passphrase prompt
	 * stuff is hooked up. Use this for clones/pushes/pulls.
	 * 
	 * @return
	 */
	public Map<String, String> getSSHEnvironment()
	{
		Map<String, String> env = new HashMap<String, String>();
		env.putAll(ShellExecutable.getEnvironment());
		IPath git_ssh = GitPlugin.getDefault().getGIT_SSH();
		if (git_ssh != null)
		{
			env.put("GIT_SSH", git_ssh.toOSString()); //$NON-NLS-1$
		}
		if (!env.isEmpty())
		{
			env = filterOutVariables(env);
		}
		return env;
	}

	/**
	 * Filter out any env vars that contain "${" in their value. Otherwise Eclipse will try to substitute and fail! TODO
	 * Maybe we can escape the ${ to avoid the issue?
	 * 
	 * @param env
	 * @return
	 */
	private static Map<String, String> filterOutVariables(Map<String, String> env)
	{
		Map<String, String> filtered = new HashMap<String, String>();
		for (Map.Entry<String, String> entry : env.entrySet())
		{
			String value = entry.getValue();
			if (value.contains("${")) //$NON-NLS-1$
			{
				continue;
			}
			filtered.put(entry.getKey(), value);
		}
		return filtered;
	}
}
