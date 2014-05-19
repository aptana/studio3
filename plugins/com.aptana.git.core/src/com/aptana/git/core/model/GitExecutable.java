/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.osgi.framework.Version;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.ShellExecutable;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.ExecutableUtil;
import com.aptana.core.util.IProcessRunner;
import com.aptana.core.util.PlatformUtil;
import com.aptana.core.util.ProcessRunnable;
import com.aptana.core.util.ProcessRunner;
import com.aptana.core.util.ProcessUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.core.util.VersionUtil;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.IDebugScopes;
import com.aptana.git.core.IPreferenceConstants;

/**
 * @author cwilliams
 */
public class GitExecutable
{
	/**
	 * Special ENV variables used for git processes.
	 */
	private static final String GIT_SSH = "GIT_SSH"; //$NON-NLS-1$
	private static final String SSH_ASKPASS = "SSH_ASKPASS"; //$NON-NLS-1$
	private static final String GIT_ASKPASS = "GIT_ASKPASS"; //$NON-NLS-1$

	/**
	 * Constants used for git version handling.
	 */
	private static final String GIT_VERSION_PREFIX = "git version "; //$NON-NLS-1$
	private static final Pattern GIT_VERSION_PATTERN = Pattern
			.compile("\\d+(\\.\\d+(\\.\\d+(\\.[a-zA-z\\d\\-_]+)?)?)?"); //$NON-NLS-1$
	public static final String MIN_GIT_VERSION = "1.6.0"; //$NON-NLS-1$

	private static final String GIT_EXECUTABLE = "git"; //$NON-NLS-1$
	protected static final String GIT_EXECUTABLE_WIN32 = GIT_EXECUTABLE + ".exe"; //$NON-NLS-1$

	/**
	 * Pattern used to sniff progress on clones.
	 */
	private static final Pattern CLONE_PERCENT_PATTERN = Pattern
			.compile("^Receiving objects:\\s+(\\d+)%\\s\\((\\d+)/(\\d+)\\).+"); //$NON-NLS-1$

	/**
	 * Common locations to look for git (initialized based on OS).
	 */
	private static List<IPath> fgLocations;

	/**
	 * Where we found git.
	 */
	private IPath gitPath;

	/**
	 * The singleton.
	 */
	static GitExecutable fgExecutable;
	private static boolean fgAddedPrefListener;

	protected GitExecutable(IPath gitPath)
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
				EclipseUtil.instanceScope().getNode(GitPlugin.getPluginId())
						.addPreferenceChangeListener(new IEclipsePreferences.IPreferenceChangeListener()
						{

							public void preferenceChange(PreferenceChangeEvent event)
							{
								if (!event.getKey().equals(IPreferenceConstants.GIT_EXECUTABLE_PATH))
									return;
								fgExecutable = null;
								// reset shell path preferences on Win32
								if (Platform.OS_WIN32.equals(Platform.getOS()))
								{
									String pathString = (String) event.getNewValue();
									if (pathString != null)
									{
										IPath path = Path.fromOSString(pathString);
										if (path.toFile().isFile())
										{
											path = path.removeLastSegments(1);
										}
										if (path.toFile().isDirectory())
										{
											ShellExecutable.setPreferenceShellPath(path);
										}
									}
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
		String pref = EclipseUtil.instanceScope().getNode(GitPlugin.PLUGIN_ID)
				.get(IPreferenceConstants.GIT_EXECUTABLE_PATH, null);
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
			IdeLog.logError(
					GitPlugin.getDefault(),
					MessageFormat
							.format("You entered a custom git path in the Preferences pane, but this path is not a valid git v{0} or higher binary. We're going to use the default search paths instead", //$NON-NLS-1$
							MIN_GIT_VERSION), IDebugScopes.DEBUG);
		}
		return null;
	}

	public static void setPreferenceGitPath(IPath path)
	{
		IEclipsePreferences prefs = EclipseUtil.instanceScope().getNode(GitPlugin.PLUGIN_ID);
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
			IdeLog.logError(GitPlugin.getDefault(), "Saving preferences failed.", e, IDebugScopes.DEBUG); //$NON-NLS-1$
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
		IdeLog.logInfo(GitPlugin.getDefault(), string);
	}

	@SuppressWarnings("nls")
	private synchronized static List<IPath> searchLocations()
	{
		if (fgLocations == null)
		{
			if (Platform.getOS().equals(Platform.OS_WIN32))
			{
				// @formatter:off
				fgLocations = CollectionsUtil.newList(
					Path.fromOSString(PlatformUtil.expandEnvironmentStrings("%PROGRAMW6432%\\Git\\bin")),
					Path.fromOSString(PlatformUtil.expandEnvironmentStrings("%PROGRAMFILES%\\Git\\bin")),
					Path.fromOSString(PlatformUtil.expandEnvironmentStrings("%PROGRAMFILES(X86)%\\Git\\bin")),
					Path.fromOSString("C:\\RailsInstaller\\Git\\bin")
				);
				// @formatter:on
			}
			else
			{
				// @formatter:off
				fgLocations = CollectionsUtil.newList(
					Path.fromOSString("/opt/local/bin"),
					Path.fromOSString("/sw/bin"),
					Path.fromOSString("/opt/git/bin"),
					Path.fromOSString("/usr/local/bin"),
					Path.fromOSString("/usr/local/git/bin"),
					Path.fromOSString(PlatformUtil.expandEnvironmentStrings("~/bin"))
				);
				// @formatter:on
			}
		}
		return fgLocations;
	}

	private static String versionForPath(IPath path)
	{
		if (path == null)
		{
			return null;
		}

		if (!path.toFile().isFile())
		{
			return null;
		}

		String version = ProcessUtil.outputForCommand(path.toOSString(), null, "--version"); //$NON-NLS-1$
		if (version != null && version.startsWith(GIT_VERSION_PREFIX))
		{
			version = version.substring(GIT_VERSION_PREFIX.length());

			// Special handling for funky msysgit version string
			if (version.contains("msysgit.")) //$NON-NLS-1$
			{
				version = version.replace("msysgit.", "msysgit_"); //$NON-NLS-1$ //$NON-NLS-2$

				// If there's still too many periods, turn ".msys" into "_msys"
				if (StringUtil.characterInstanceCount(version, '.') > 3)
				{
					version = version.replace(".msysgit", "_msysgit"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}

			// Now grab the version out using a regexp
			Matcher m = GIT_VERSION_PATTERN.matcher(version);
			if (m.find())
			{
				return m.group();
			}
			return version;
		}

		return null;
	}

	public static boolean acceptBinary(IPath path)
	{
		if (path == null)
		{
			return false;
		}

		String version = versionForPath(path);
		if (version == null)
		{
			return false;
		}

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
		List<String> arguments = new ArrayList<String>(Arrays.asList(args));
		arguments.add(0, gitPath.toOSString());
		return new ProcessRunner().runInBackground(workingDir, arguments.toArray(new String[arguments.size()]));
	}

	IStatus runInBackground(IPath workingDir, Map<String, String> env, String... args)
	{
		// FIXME Inline into GitRepository.execute?
		List<String> arguments = new ArrayList<String>(Arrays.asList(args));
		arguments.add(0, gitPath.toOSString());
		return new ProcessRunner().runInBackground(workingDir, env, arguments.toArray(new String[arguments.size()]));
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
		List<String> arguments = new ArrayList<String>(Arrays.asList(args));
		arguments.add(0, gitPath.toOSString());
		return new ProcessRunner().runInBackground(workingDirectory, null, input, arguments);
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
		List<String> args = new ArrayList<String>(Arrays.asList(arguments));
		args.add(0, gitPath.toOSString());
		return new ProcessRunner().run(directory, args.toArray(new String[args.size()]));
	}

	/**
	 * Sets up the environment map in a way that our special GIT_SSH/GIT_ASKPASS env variables are set so that the SSH
	 * passphrase/HTTPS prompt stuff is hooked up. Use this for clones/pushes/pulls.
	 * 
	 * @return
	 */
	public static Map<String, String> getEnvironment()
	{
		Map<String, String> env = new HashMap<String, String>();
		env.putAll(ShellExecutable.getEnvironment());
		IPath git_ssh = GitPlugin.getDefault().getGIT_SSH();
		if (git_ssh != null)
		{
			env.put(GIT_SSH, git_ssh.toOSString());
		}
		IPath ssh_askpass = GitPlugin.getDefault().getSSH_ASKPASS();
		if (ssh_askpass != null)
		{
			env.put(SSH_ASKPASS, ssh_askpass.toOSString());
		}
		IPath git_askpass = GitPlugin.getDefault().getGIT_ASKPASS();
		if (git_askpass != null)
		{
			env.put(GIT_ASKPASS, git_askpass.toOSString());
		}
		if (Platform.OS_WIN32.equals(Platform.getOS()))
		{
			env.remove("PATH"); //$NON-NLS-1$
			env.remove("PWD"); //$NON-NLS-1$
			String path = System.getenv("Path"); //$NON-NLS-1$
			env.put("Path", instance().path().removeLastSegments(1).toOSString() + File.pathSeparator + path); //$NON-NLS-1$
		}
		return filterOutVariables(env);
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
		for (Iterator<Map.Entry<String, String>> i = env.entrySet().iterator(); i.hasNext();)
		{
			String value = i.next().getValue();
			if (value.contains("${")) //$NON-NLS-1$
			{
				i.remove();
			}
		}
		return env;
	}

	/**
	 * Returns the version of git pointed at.
	 * 
	 * @return
	 */
	public Version version()
	{
		String versionString = GitExecutable.versionForPath(gitPath);
		if (versionString == null)
		{
			return Version.emptyVersion;
		}

		try
		{
			return VersionUtil.parseVersion(versionString);
		}
		catch (Exception ex)
		{
			IdeLog.logError(GitPlugin.getDefault(),
					MessageFormat.format(Messages.GitExecutable_UnableToParseGitVersion, versionString), ex,
					IDebugScopes.DEBUG);
			return Version.emptyVersion;
		}
	}

	/**
	 * Clones a git repo to a local location.
	 * 
	 * @param sourceURI
	 *            The "uri" that you would typically pass as your first arg after "git clone" on the command line. This
	 *            can be something like "git@github.com:username/repo_name.git" or
	 *            http[s]://host.xz[:port]/path/to/repo.git/ See http://linux.die.net/man/1/git-clone
	 * @param dest
	 *            Where should the clone be located locally? An absolute IPath is expected.
	 * @param shallow
	 *            a boolean indicating whether or not we want the full history of the repo. If the clone is going to be
	 *            temporary, discarded or disconnected from git you should specify a shallow clone.
	 * @param monitor
	 * @return
	 * @throws CoreException
	 */
	public IStatus clone(String sourceURI, IPath dest, boolean shallow, IProgressMonitor monitor)
	{
		SubMonitor subMonitor = SubMonitor.convert(monitor, 1000);
		try
		{
			Version version = version();
			boolean includeProgress = version.compareTo(new Version(1, 7, 0)) >= 0;

			Map<String, String> env = GitExecutable.getEnvironment();
			List<String> args = new ArrayList<String>();
			args.add("clone"); //$NON-NLS-1$
			if (shallow)
			{
				args.add("--depth"); //$NON-NLS-1$
				args.add("1"); //$NON-NLS-1$
			}
			// Use --progress switch if git version is 1.7+!
			if (includeProgress)
			{
				args.add("--progress"); //$NON-NLS-1$
			}
			args.add("--"); //$NON-NLS-1$
			args.add(sourceURI);
			args.add(dest.toOSString());
			// Now run it!
			Process p = run(env, args.toArray(new String[args.size()]));
			if (p == null)
			{
				return new Status(IStatus.ERROR, GitPlugin.getPluginId(), MessageFormat.format(
						Messages.GitExecutable_UnableToLaunchCloneError, sourceURI, dest));
			}

			subMonitor.worked(100);
			CloneRunnable runnable = new CloneRunnable(p, subMonitor.newChild(900));
			Thread t = new Thread(runnable);
			t.start();
			t.join();

			return runnable.getResult();
		}
		catch (CoreException e)
		{
			IdeLog.log(GitPlugin.getDefault(), e.getStatus());
			return e.getStatus();
		}
		catch (Throwable e)
		{
			IdeLog.logError(GitPlugin.getDefault(), e, IDebugScopes.DEBUG);
			return new Status(IStatus.ERROR, GitPlugin.getPluginId(), e.getMessage(), e);
		}
		finally
		{
			subMonitor.done();
		}
	}

	protected Process run(Map<String, String> env, String... args) throws IOException, CoreException
	{
		List<String> arguments = new ArrayList<String>(Arrays.asList(args));
		arguments.add(0, path().toOSString());
		return createProcessRunner().run(null, env, arguments.toArray(new String[arguments.size()]));
	}

	protected IProcessRunner createProcessRunner()
	{
		return new ProcessRunner();
	}

	/**
	 * A Runnable which sniffs the output of a git clone operation to provide progress for an IProgressMonitor.
	 * 
	 * @author cwilliams
	 */
	static class CloneRunnable extends ProcessRunnable
	{

		private int lastPercent;

		CloneRunnable(Process p, IProgressMonitor monitor)
		{
			super(p, monitor, false);
		}

		@Override
		protected void handleLine(String line)
		{
			super.handleLine(line);

			// Read in the line and see if we can sniff progress
			Matcher m = CLONE_PERCENT_PATTERN.matcher(line);
			if (m.find())
			{
				String percent = m.group(1);
				int percentInt = Integer.parseInt(percent);
				if (percentInt > lastPercent)
				{
					monitor.worked(percentInt - lastPercent);
					lastPercent = percentInt;
				}
			}
		}

		public void run()
		{
			lastPercent = 0;
			super.run();
		}
	}
}
