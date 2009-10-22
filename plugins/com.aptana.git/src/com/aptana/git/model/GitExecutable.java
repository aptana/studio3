package com.aptana.ide.red.git.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.debug.core.ILaunch;

import com.aptana.ide.red.git.Activator;
import com.aptana.ide.red.git.ProcessUtil;

public class GitExecutable
{

	private static final String MIN_GIT_VERSION = "1.6.0";
	private static ArrayList<String> fgLocations;
	private String gitPath;

	private GitExecutable(String gitPath)
	{
		this.gitPath = gitPath;
	}

	public static GitExecutable find()
	{
		// Check what we might have in user defaults
		// NOTE: Currently this should NOT have a registered default, or the searching bits below won't work
		// gitPath = [[NSUserDefaults standardUserDefaults] stringForKey:@"gitExecutable"];
		// if (gitPath.length > 0) {
		// if (acceptBinary(gitPath))
		// return;
		// [[NSAlert alertWithMessageText:@"Invalid git path"
		// defaultButton:@"OK"
		// alternateButton:nil
		// otherButton:nil
		// informativeTextWithFormat:@"You entered a custom git path in the Preferences pane, "
		// "but this path is not a valid git v" MIN_GIT_VERSION " or higher binary. We're going to use the default "
		// "search paths instead"] runModal];
		// }

		// Try to find the path of the Git binary
		String gitPath = System.getenv("GIT_PATH");
		if (gitPath != null && acceptBinary(gitPath))
			return new GitExecutable(gitPath);

		// No explicit path. Try it with "which"
		String whichPath = ProcessUtil.outputForCommand("/usr/bin/which", null, "git");
		if (acceptBinary(whichPath))
			return new GitExecutable(whichPath);

		// Still no path. Let's try some default locations.
		for (String location : searchLocations())
		{
			if (acceptBinary(location))
				return new GitExecutable(location);
		}

		log("Could not find a git binary higher than version " + MIN_GIT_VERSION);
		return null;
	}

	private static void log(String string)
	{
		Activator.logInfo(string);
	}

	private static List<String> searchLocations()
	{
		if (fgLocations == null)
		{
			fgLocations = new ArrayList<String>();
			fgLocations.add("/opt/local/bin/git");
			fgLocations.add("/sw/bin/git");
			fgLocations.add("/opt/git/bin/git");
			fgLocations.add("/usr/local/bin/git");
			fgLocations.add("/usr/local/git/bin/git");
			fgLocations.add(stringByExpandingTildeInPath("~/bin/git"));
		}
		return fgLocations;
	}

	private static String stringByExpandingTildeInPath(String string)
	{
		String userHome = System.getProperty("user.home");
		return string.replaceAll("~", userHome);
	}

	public static String versionForPath(String path)
	{
		if (path == null)
			return null;

		File file = new File(path);
		if (!file.isFile())
			return null;

		String version = ProcessUtil.outputForCommand(path, null, "--version");
		if (version.startsWith("git version "))
			return version.substring(12);

		return null;
	}

	public static boolean acceptBinary(String path)
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

		log("Found a git binary at " + path + ", but is only version " + version);
		return false;
	}

	public String path()
	{
		return gitPath;
	}

	public ILaunch run(String workingDir, String... args)
	{
		return ProcessUtil.run(gitPath, workingDir, args);
	}

	public String outputForCommand(String workingDir, String... args)
	{
		return ProcessUtil.outputForCommand(gitPath, workingDir, args);
	}

	public Map<Integer, String> runInBackground(String workingDir, String... args)
	{
		return ProcessUtil.runInBackground(gitPath, workingDir, args);
	}

	public Map<Integer, String> runInBackground(String workingDirectory, Map<String, String> amendEnvironment,
			String... args)
	{
		return ProcessUtil.runInBackground(gitPath, workingDirectory, amendEnvironment, args);
	}

	public Map<Integer, String> runInBackground(String workingDirectory, String input,
			Map<String, String> amendEnvironment, String... args)
	{
		return ProcessUtil.runInBackground(gitPath, workingDirectory, input, amendEnvironment, args);
	}

}
