package com.aptana.util;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.Platform;

/**
 * This class is meant as a utility for searching for an executable on the PATH, and/or in a set of common locations.
 */
public abstract class ExecutableUtil
{

	/**
	 * @param exeName
	 *            name of the binary. ".exe" is appended for windows when searching the path.
	 * @param prefPath
	 *            Path specified in user's preferences.
	 * @param searchLocations
	 *            Common locations to search.
	 * @return
	 */
	public static String find(String exeName, String prefPath, List<String> searchLocations)
	{
		if (prefPath != null && prefPath.length() > 0)
		{
			if (acceptBinary(prefPath))
			{
				return prefPath;
			}
		}

		if (Platform.getOS().equals(Platform.OS_WIN32))
		{
			// Grab PATH and search it!
			String path = System.getenv("PATH"); //$NON-NLS-1$
			String[] paths = path.split(File.pathSeparator);
			for (String pathString : paths)
			{
				String possiblePath = pathString + File.separator + exeName + ".exe"; //$NON-NLS-1$
				if (acceptBinary(possiblePath))
				{
					return possiblePath;
				}
			}
		}
		else
		{
			// No explicit path. Try it with "which"
			String whichPath = ProcessUtil.outputForCommand("/usr/bin/which", null, exeName); //$NON-NLS-1$ //$NON-NLS-2$
			if (acceptBinary(whichPath))
				return whichPath;
		}

		// Still no path. Let's try some default locations.
		for (String location : searchLocations)
		{
			if (acceptBinary(location))
				return location;
		}
		return null;
	}

	private static boolean acceptBinary(String prefPath)
	{
		File file = new File(prefPath);
		return file.exists() && file.canExecute();
	}

}
