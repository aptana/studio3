package com.aptana.core.util;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.aptana.core.ShellExecutable;

/**
 * This class is meant as a utility for searching for an executable on the PATH, and/or in a set of common locations.
 */
public abstract class ExecutableUtil
{

	/**
	 * @param executableName
	 *            name of the binary.
	 * @param appendExtension
	 *            ".exe" is appended for windows when searching the PATH.
	 * @param searchLocations
	 *            Common locations to search.
	 * @return
	 */
	public static IPath find(String executableName, boolean appendExtension, List<IPath> searchLocations)
	{
		Map<String, String> env = ShellExecutable.getEnvironment();
		if (Platform.OS_WIN32.equals(Platform.getOS()))
		{
			String pathENV = System.getenv("PATH");
			if (env != null && !env.isEmpty())
			{
				pathENV = env.get("PATH");
			}
			// Grab PATH and search it!
			String[] paths = pathENV.split(File.pathSeparator);
			for (String pathString : paths)
			{
				IPath path = Path.fromOSString(pathString).append(executableName);
				IPath result = findExecutable(path, appendExtension);
				if (result != null)
				{
					return result;
				}
			}
		}
		else
		{
			// No explicit path. Try it with "which"
			String whichResult = ProcessUtil.outputForCommand("/usr/bin/which", null, env, executableName); //$NON-NLS-1$
			if (whichResult != null && whichResult.trim().length() > 0)
			{
				IPath whichPath = Path.fromOSString(whichResult.trim());
				if (isExecutable(whichPath))
					return whichPath;
			}
		}

		// Still no path. Let's try some default locations.
		if (searchLocations != null)
		{
			for (IPath location : searchLocations)
			{
				IPath result = findExecutable(location.append(executableName), appendExtension);
				if (result != null)
					return result;
			}
		}

		return null;
	}

	private static IPath findExecutable(IPath basename, boolean appendExtension)
	{
		if (Platform.OS_WIN32.equals(Platform.getOS()) && appendExtension)
		{
			String[] extensions = System.getenv("PATHEXT").split(File.pathSeparator); //$NON-NLS-1$
			for (String ext : extensions)
			{
				if (ext.startsWith(".")) //$NON-NLS-1$
					ext = ext.substring(1);
				IPath pathWithExt = basename.addFileExtension(ext);
				if (isExecutable(pathWithExt))
				{
					return pathWithExt;
				}
			}

		}
		else if (isExecutable(basename))
		{
			return basename;
		}
		return null;
	}

	private static boolean isExecutable(IPath path)
	{
		File file = path.toFile();
		return file.exists() && file.canExecute();
	}

}
