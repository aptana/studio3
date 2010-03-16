package com.aptana.git.core.model;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.IPreferenceConstants;
import com.aptana.util.ProcessUtil;

public class GitExecutable
{

	public static final String MIN_GIT_VERSION = "1.6.0"; //$NON-NLS-1$
	private static ArrayList<String> fgLocations;
	private String gitPath;

	static GitExecutable fgExecutable;
	private static boolean fgAddedPrefListener;

	private GitExecutable(String gitPath)
	{
		this.gitPath = gitPath;
	}

	public static GitExecutable instance()
	{
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
		String prefPath = Platform.getPreferencesService().getString(GitPlugin.getPluginId(), IPreferenceConstants.GIT_EXECUTABLE_PATH, null, null);
		if (prefPath != null && prefPath.length() > 0)
		{
			if (acceptBinary(prefPath))
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
		
		if (Platform.getOS().equals(Platform.OS_WIN32))
		{
			// Grab PATH and search it!
			String path = System.getenv("PATH"); //$NON-NLS-1$
			String[] paths = path.split(File.pathSeparator);
			for (String pathString : paths)
			{
				String possiblePath = pathString + File.separator + "git.exe"; //$NON-NLS-1$
				if (acceptBinary(possiblePath))
				{
					return new GitExecutable(possiblePath);
				}
			}
		}
		else
		{
			// No explicit path. Try it with "which"
			String whichPath = ProcessUtil.outputForCommand("/usr/bin/which", null, "git"); //$NON-NLS-1$ //$NON-NLS-2$
			if (acceptBinary(whichPath))
				return new GitExecutable(whichPath);
		}
		
		// Still no path. Let's try some default locations.
		for (String location : searchLocations())
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

	private static List<String> searchLocations()
	{
		if (fgLocations == null)
		{
			fgLocations = new ArrayList<String>();
			if (Platform.getOS().equals(Platform.OS_WIN32))
			{
				fgLocations.add("C:\\Program Files (x86)\\Git\\bin\\git.exe"); //$NON-NLS-1$
				fgLocations.add("C:\\Program Files\\Git\\bin\\git.exe"); //$NON-NLS-1$
			}
			else
			{
				fgLocations.add("/opt/local/bin/git"); //$NON-NLS-1$
				fgLocations.add("/sw/bin/git"); //$NON-NLS-1$
				fgLocations.add("/opt/git/bin/git"); //$NON-NLS-1$
				fgLocations.add("/usr/local/bin/git"); //$NON-NLS-1$
				fgLocations.add("/usr/local/git/bin/git"); //$NON-NLS-1$
				fgLocations.add(stringByExpandingTildeInPath("~/bin/git")); //$NON-NLS-1$
			}
		}
		return fgLocations;
	}

	private static String stringByExpandingTildeInPath(String string)
	{
		String userHome = System.getProperty("user.home"); //$NON-NLS-1$
		return string.replaceAll("~", userHome); //$NON-NLS-1$
	}

	private static String versionForPath(String path)
	{
		if (path == null)
			return null;

		File file = new File(path);
		if (!file.isFile())
			return null;

		String version = ProcessUtil.outputForCommand(path, null, "--version"); //$NON-NLS-1$
		if (version != null && version.startsWith("git version ")) //$NON-NLS-1$
			return version.substring(12);

		return null;
	}

	public static boolean acceptBinary(String path)
	{
		if (path == null || path.length() == 0)
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

	public String path()
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
	public String outputForCommand(String workingDir, String... args)
	{
		Map<String, String> env = null;
		IPath git_ssh = GitPlugin.getDefault().getGIT_SSH();
		if (git_ssh != null) {
			env = new HashMap<String, String>();
			env.put("GIT_SSH", git_ssh.toOSString()); //$NON-NLS-1$
		}
		return ProcessUtil.outputForCommand(gitPath, workingDir, env, args);
	}

	/**
	 * Launches the git process and returns a map from the exit value to the stdout output read in.
	 * 
	 * @param workingDir
	 * @param args
	 * @return
	 */
	public Map<Integer, String> runInBackground(String workingDir, String... args)
	{
		return ProcessUtil.runInBackground(gitPath, workingDir, args);
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
	public Map<Integer, String> runInBackground(String workingDirectory, String input,
			Map<String, String> amendEnvironment, String... args)
	{
		IPath git_ssh = GitPlugin.getDefault().getGIT_SSH();
		if (git_ssh != null) {
			if (amendEnvironment == null) {
				amendEnvironment = new HashMap<String, String>();
			}
			amendEnvironment.put("GIT_SSH", git_ssh.toOSString()); //$NON-NLS-1$
		}
		return ProcessUtil.runInBackground(gitPath, workingDirectory, input, amendEnvironment, args);
	}

	/**
	 * Launches the git process and returns the handle to the active process.
	 * 
	 * @param directory
	 * @param arguments
	 * @return
	 * @throws IOException
	 */
	public Process run(String directory, String... arguments) throws IOException
	{
		return ProcessUtil.run(gitPath, directory, arguments);
	}
}
