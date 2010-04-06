package com.aptana.util;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

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
	 * @param preferencesPath
	 *            Path specified in user's preferences.
	 * @param searchLocations
	 *            Common locations to search.
	 * @return
	 */
	public static IPath find(String executableName, boolean appendExtension, IPath preferencesPath, List<IPath> searchLocations)
	{
		if (preferencesPath != null) {
			if (isExecutable(preferencesPath)) {
				return preferencesPath;
			}
		}

		if (Platform.OS_WIN32.equals(Platform.getOS())) {
			// Grab PATH and search it!
			String[] paths = System.getenv("PATH").split(File.pathSeparator); //$NON-NLS-1$
			for (String pathString : paths) {
				IPath path = Path.fromOSString(pathString).append(executableName);
				if (appendExtension) {
					IPath result = findExecutable(path);
					if (result != null) {
						return result;
					}
				} else if (isExecutable(path)) {
					return path;
				}
			}
		} else {
			// No explicit path. Try it with "which"
			String whichResult = ProcessUtil.outputForCommand("/usr/bin/which", null, executableName); //$NON-NLS-1$
			if (whichResult != null && whichResult.trim().length() > 0) {
				IPath whichPath = Path.fromOSString(whichResult.trim());
				if (isExecutable(whichPath))
					return whichPath;
			}
		}

		// Still no path. Let's try some default locations.
		for (IPath location : searchLocations) {
			IPath result = findExecutable(location.append(executableName));
			if (result != null)
				return result;
		}
		return null;
	}
	
	private static IPath findExecutable(IPath basename) {
		if (Platform.OS_WIN32.equals(Platform.getOS())) {
			String[] extensions = System.getenv("PATHEXT").split(File.pathSeparator); //$NON-NLS-1$
			for (String ext : extensions) {
				IPath pathWithExt = basename.addFileExtension(ext);
				if (isExecutable(pathWithExt)) {
					return pathWithExt;
				}
			}
			
		} else if (isExecutable(basename)) {
			return basename;
		}
		return null;
	}

	private static boolean isExecutable(IPath path) {
		File file = path.toFile();
		return file.exists() && file.canExecute();
	}

}
