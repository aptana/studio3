/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.aptana.core.CorePlugin;
import com.aptana.core.IDebugScopes;
import com.aptana.core.ShellExecutable;
import com.aptana.core.logging.IdeLog;

/**
 * This class is meant as a utility for searching for an executable on the PATH, and/or in a set of common locations.
 */
public final class ExecutableUtil
{

	private static final String PATHEXT = "PATHEXT"; //$NON-NLS-1$
	@SuppressWarnings("unused")
	private static final String WHICH_PATH = "/usr/bin/which"; //$NON-NLS-1$
	private static final String PATH = "PATH"; //$NON-NLS-1$

	private ExecutableUtil()
	{
	}

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
		return find(executableName, appendExtension, searchLocations, (FileFilter) null);
	}

	public static IPath find(String executableName, boolean appendExtension, List<IPath> searchLocations,
			IPath workingDirectory)
	{
		return find(executableName, appendExtension, searchLocations, null, workingDirectory);
	}

	/**
	 * @param executableName
	 *            name of the binary.
	 * @param appendExtension
	 *            ".exe" is appended for windows when searching the PATH.
	 * @param searchLocations
	 *            Common locations to search.
	 * @param filter
	 *            File filter
	 * @return
	 */
	public static IPath find(String executableName, boolean appendExtension, List<IPath> searchLocations,
			FileFilter filter)
	{
		return find(executableName, appendExtension, searchLocations, filter, null);
	}

	/**
	 * @param executableName
	 *            name of the binary.
	 * @param appendExtension
	 *            ".exe" is appended for windows when searching the PATH.
	 * @param searchLocations
	 *            Common locations to search.
	 * @param filter
	 *            File filter
	 * @param workingDirectory
	 * @return
	 */
	public static IPath find(String executableName, boolean appendExtension, List<IPath> searchLocations,
			FileFilter filter, IPath workingDirectory)
	{
		if (executableName == null)
		{
			return null;
		}

		// Grab PATH from shell if possible
		Map<String, String> env = ShellExecutable.getEnvironment(workingDirectory);
		String pathENV;
		if (env != null && env.containsKey(PATH))
		{
			pathENV = PathUtil.convertPATH(env.get(PATH));
		}
		else
		{
			pathENV = System.getenv(PATH);
		}

		boolean infoLoggingEnabled = IdeLog.isInfoEnabled(CorePlugin.getDefault(), IDebugScopes.SHELL);
		// Grab PATH...
		String[] paths = pathENV.split(File.pathSeparator);
		if (infoLoggingEnabled)
		{
			IdeLog.logInfo(
					CorePlugin.getDefault(),
					MessageFormat.format(
							"Searching for {0} in PATH locations: {1}", executableName, StringUtil.join(", ", paths)), IDebugScopes.SHELL); //$NON-NLS-1$ //$NON-NLS-2$
		}
		// Now search the PATH locations
		for (String pathString : paths)
		{
			IPath path = Path.fromOSString(pathString).append(executableName);
			IPath result = findExecutable(path, appendExtension);
			if (result != null && (filter == null || filter.accept(result.toFile())))
			{
				if (infoLoggingEnabled)
				{
					IdeLog.logInfo(CorePlugin.getDefault(),
							MessageFormat.format("Found executable on PATH: {0}", result), IDebugScopes.SHELL); //$NON-NLS-1$
				}
				return result;
			}
		}

		// Still no path. Let's try some default locations.
		return findInLocations(executableName, appendExtension, searchLocations, filter);
	}

	/**
	 * Finds the executable only in the specified search locations.
	 * 
	 * @param executableName
	 * @param appendExtension
	 * @param searchLocations
	 * @param filter
	 * @param workingDirectory
	 * @return
	 */
	public static IPath findInLocations(String executableName, boolean appendExtension, List<IPath> searchLocations,
			FileFilter filter)
	{
		boolean infoLoggingEnabled = IdeLog.isInfoEnabled(CorePlugin.getDefault(), IDebugScopes.SHELL);

		if (searchLocations != null)
		{
			for (IPath location : searchLocations)
			{
				IPath result = findExecutable(location.append(executableName), appendExtension);
				if (result != null && (filter == null || filter.accept(result.toFile())))
				{
					if (infoLoggingEnabled)
					{
						IdeLog.logInfo(
								CorePlugin.getDefault(),
								MessageFormat.format("Found executable at common location: {0}", result), IDebugScopes.SHELL); //$NON-NLS-1$
					}
					return result;
				}
			}
		}
		return null;
	}

	private static IPath findExecutable(IPath basename, boolean appendExtension)
	{
		if (Platform.OS_WIN32.equals(Platform.getOS()) && appendExtension)
		{
			String[] extensions = System.getenv(PATHEXT).split(File.pathSeparator);
			for (String ext : extensions)
			{
				if (ext.length() > 0 && ext.charAt(0) == '.')
				{
					ext = ext.substring(1);
				}
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

	public static boolean isExecutable(IPath path)
	{
		if (path == null)
		{
			return false;
		}
		File file = path.toFile();
		if (file == null || !file.exists() || file.isDirectory())
		{
			return false;
		}

		// OK, file exists
		try
		{
			Method m = File.class.getMethod("canExecute"); //$NON-NLS-1$
			if (m != null)
			{
				return (Boolean) m.invoke(file);
			}
		}
		catch (Exception e)
		{
		}

		// File.canExecute() doesn't exist; do our best to determine if file is executable...
		if (Platform.OS_WIN32.equals(Platform.getOS()))
		{
			return true;
		}
		IFileStore fileStore = EFS.getLocalFileSystem().getStore(path);
		return fileStore.fetchInfo().getAttribute(EFS.ATTRIBUTE_EXECUTABLE);
	}

	public static boolean isGemInstallable()
	{
		if (!Platform.OS_WIN32.equals(Platform.getOS()))
		{
			// TODO This code is pretty blase about possible nulls/errors/etc. Should probably try and make it
			// more bullet-proof.

			// grab the path to the gem executable dir
			IPath gemBin = find("gem", true, null); //$NON-NLS-1$
			String output = ProcessUtil.outputForCommand(gemBin.toOSString(), null, "environment"); //$NON-NLS-1$
			final String searchString = "EXECUTABLE DIRECTORY:"; //$NON-NLS-1$
			int index = output.indexOf(searchString);
			output = output.substring(index + searchString.length());
			// find first newline...
			output = output.split("\r\n|\r|\n")[0].trim(); //$NON-NLS-1$
			// Now see if user has rights to write to this dir to determine if we need to run under sudo
			return new File(output).canWrite();
		}
		return true;
	}
}
