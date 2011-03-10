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
import java.lang.reflect.InvocationTargetException;
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
import com.aptana.core.ShellExecutable;

/**
 * This class is meant as a utility for searching for an executable on the PATH, and/or in a set of common locations.
 */
public final class ExecutableUtil
{

	private static final String PATHEXT = "PATHEXT"; //$NON-NLS-1$
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
		Map<String, String> env = ShellExecutable.getEnvironment(workingDirectory);
		if (Platform.OS_WIN32.equals(Platform.getOS()))
		{
			String[] paths;
			if (env != null && env.containsKey(PATH))
			{
				paths = env.get(PATH).split(ShellExecutable.PATH_SEPARATOR);
				for (int i = 0; i < paths.length; ++i)
				{
					if (paths[i].matches("^/(.)/.*")) { //$NON-NLS-1$
						paths[i] = paths[i].replaceFirst("^/(.)/", "$1:/"); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
			}
			else
			{
				String pathENV = System.getenv(PATH);
				paths = pathENV.split(File.pathSeparator);
			}
			// Grab PATH and search it!
			for (String pathString : paths)
			{
				IPath path = Path.fromOSString(pathString).append(executableName);
				IPath result = findExecutable(path, appendExtension);
				if (result != null && (filter == null || filter.accept(result.toFile())))
				{
					return result;
				}
			}
		}
		else
		{
			// No explicit path. Try it with "which"
			String whichResult = ProcessUtil.outputForCommand(WHICH_PATH, workingDirectory, env, executableName);
			if (whichResult != null && whichResult.trim().length() > 0)
			{
				IPath whichPath = Path.fromOSString(whichResult.trim());
				if (isExecutable(whichPath) && (filter == null || filter.accept(whichPath.toFile())))
				{
					CorePlugin.logInfo(MessageFormat.format("Found executable via 'which': {0}", whichPath)); //$NON-NLS-1$
					return whichPath;
				}
			}
		}

		// Still no path. Let's try some default locations.
		if (searchLocations != null)
		{
			for (IPath location : searchLocations)
			{
				IPath result = findExecutable(location.append(executableName), appendExtension);
				if (result != null && (filter == null || filter.accept(result.toFile())))
				{
					CorePlugin.logInfo(MessageFormat.format("Found executable at common location: {0}", result)); //$NON-NLS-1$
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

	public static boolean isExecutable(IPath path)
	{
		if (path == null)
		{
			return false;
		}
		File file = path.toFile();
		if (file == null || !file.exists())
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
		} catch (NoSuchMethodException e) {
			// ignore, only available on Java 6+
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}

		// File.canExecute() doesn't exist; do our best to determine if file is executable...
		if (Platform.OS_WIN32.equals(Platform.getOS()))
		{
			return true;
		}
		IFileStore fileStore = EFS.getLocalFileSystem().getStore(path);
		return fileStore.fetchInfo().getAttribute(EFS.ATTRIBUTE_EXECUTABLE);
	}
}
