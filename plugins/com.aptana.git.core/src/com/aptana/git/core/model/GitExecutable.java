package com.aptana.git.core.model;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;

import com.aptana.core.ShellExecutable;
import com.aptana.core.util.PlatformUtil;
import com.aptana.core.util.ProcessUtil;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.IPreferenceConstants;

public class GitExecutable
{

	public static final String MIN_GIT_VERSION = "1.6.0"; //$NON-NLS-1$
	private static ArrayList<IPath> fgLocations;
	private IPath gitPath;

	static GitExecutable fgExecutable;
	private static boolean fgAddedPrefListener;

	private GitExecutable(IPath gitPath)
	{
		this.gitPath = gitPath;
	}

	public static GitExecutable instance()
	{
		// FIXME Singletons are bad! hid behind an interface and grab the global instance of this from the plugin?
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
							}
						});
				fgAddedPrefListener = true;
			}
		}
		return fgExecutable;
	}

	private static GitExecutable find()
	{
		String pref = Platform.getPreferencesService().getString(GitPlugin.getPluginId(), IPreferenceConstants.GIT_EXECUTABLE_PATH, null, null);
		if (pref != null)
		{
			IPath prefPath = Path.fromOSString(pref);
			if (!prefPath.isEmpty() && acceptBinary(prefPath))
			{
				return new GitExecutable(prefPath);
			}
			GitPlugin
					.logError(
							MessageFormat
									.format(
											"You entered a custom git path in the Preferences pane, but this path is not a valid git v{0} or higher binary. We're going to use the default search paths instead", //$NON-NLS-1$
											MIN_GIT_VERSION), null);
		}
		
		if (Platform.OS_WIN32.equals(Platform.getOS()))
		{
			// Grab PATH and search it!
			String path = System.getenv("PATH"); //$NON-NLS-1$
			String[] paths = path.split(File.pathSeparator);
			// If the user is using msysgit we prefer using git.cmd wrapper
			// instead of the git.exe because it sets the HOME variable
			// correctly which in turn allows the ssh to find the
			// ${HOME}/.ssh folder.
			for (String extension : new String[] {"cmd", "exe"}) //$NON-NLS-1$ //$NON-NLS-2$
			{
				for (String pathString : paths)
				{
					IPath possiblePath = Path.fromOSString(pathString).append("git").addFileExtension(extension); //$NON-NLS-1$
					if (acceptBinary(possiblePath))
					{
						return new GitExecutable(possiblePath);
					}
				}
			}
		}
		else
		{
			// No explicit path. Try it with "which"
			IPath whichPath = Path.fromOSString(ProcessUtil.outputForCommand("/usr/bin/which", null, "git")); //$NON-NLS-1$ //$NON-NLS-2$
			if (!whichPath.isEmpty() && acceptBinary(whichPath))
				return new GitExecutable(whichPath);
		}
		
		// Still no path. Let's try some default locations.
		for (IPath location : searchLocations())
		{
			if (acceptBinary(location))
				return new GitExecutable(location);
		}

		log(MessageFormat.format("Could not find a git binary higher than version {0}", MIN_GIT_VERSION)); //$NON-NLS-1$
		return null;
	}

	private static void log(String string)
	{
		GitPlugin.logInfo(string);
	}

	private static List<IPath> searchLocations()
	{
		if (fgLocations == null)
		{
			fgLocations = new ArrayList<IPath>();
			if (Platform.getOS().equals(Platform.OS_WIN32))
			{
				fgLocations.add(Path.fromOSString(PlatformUtil.expandEnvironmentStrings("%PROGRAMW6432%\\Git\\cmd\\git.cmd"))); //$NON-NLS-1$
				fgLocations.add(Path.fromOSString(PlatformUtil.expandEnvironmentStrings("%PROGRAMFILES%\\Git\\cmd\\git.cmd"))); //$NON-NLS-1$
				fgLocations.add(Path.fromOSString(PlatformUtil.expandEnvironmentStrings("%PROGRAMFILES(X86)%\\Git\\cmd\\git.cmd"))); //$NON-NLS-1$
			}
			else
			{
				fgLocations.add(Path.fromOSString("/opt/local/bin/git")); //$NON-NLS-1$
				fgLocations.add(Path.fromOSString("/sw/bin/git")); //$NON-NLS-1$
				fgLocations.add(Path.fromOSString("/opt/git/bin/git")); //$NON-NLS-1$
				fgLocations.add(Path.fromOSString("/usr/local/bin/git")); //$NON-NLS-1$
				fgLocations.add(Path.fromOSString("/usr/local/git/bin/git")); //$NON-NLS-1$
				fgLocations.add(Path.fromOSString(PlatformUtil.expandEnvironmentStrings("~/bin/git"))); //$NON-NLS-1$
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
	 * Launches the git process and returns a map from the exit value to the stdout output read in.
	 * 
	 * @param workingDir
	 * @param args
	 * @return
	 */
	public String outputForCommand(IPath workingDir, String... args)
	{
		Map<String, String> env = new HashMap<String, String>();
		env.putAll(ShellExecutable.getEnvironment());
		IPath git_ssh = GitPlugin.getDefault().getGIT_SSH();
		if (git_ssh != null) {
			env.put("GIT_SSH", git_ssh.toOSString()); //$NON-NLS-1$
		}
		return ProcessUtil.outputForCommand(gitPath.toOSString(), workingDir, env, args);
	}

	/**
	 * Launches the git process and returns a map from the exit value to the stdout output read in.
	 * 
	 * @param workingDir
	 * @param args
	 * @return
	 */
	public Map<Integer, String> runInBackground(IPath workingDir, String... args)
	{
		return ProcessUtil.runInBackground(gitPath.toOSString(), workingDir, args);
	}

	/**
	 * Launches the git process and returns a map from the exit value to the stdout output read in.
	 * 
	 * @param workingDirectory
	 * @param input
	 * @param amendEnvironment
	 * @param args
	 * @return
	 */
	public Map<Integer, String> runInBackground(IPath workingDirectory, String input,
			Map<String, String> amendEnvironment, String... args)
	{
		Map<String, String> env = new HashMap<String, String>();
		env.putAll(ShellExecutable.getEnvironment());
		if (amendEnvironment != null) {
			env.putAll(amendEnvironment);
		}

		IPath git_ssh = GitPlugin.getDefault().getGIT_SSH();
		if (git_ssh != null) {
			env.put("GIT_SSH", git_ssh.toOSString()); //$NON-NLS-1$
		}
		return ProcessUtil.runInBackground(gitPath.toOSString(), workingDirectory, input, env, args);
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
	public Process run(IPath directory, String... arguments) throws IOException, CoreException
	{
		return ProcessUtil.run(gitPath.toOSString(), directory, arguments);
	}
}
