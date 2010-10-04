/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.aptana.core.ShellExecutable;

/**
 * This class is meant as a utility for searching for an executable on the PATH, and/or in a set of common locations.
 */
public final class ExecutableUtil
{

	private ExecutableUtil() {
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
		return find(executableName, appendExtension, searchLocations, null);
	}

	/**
	 * @param executableName
	 *            name of the binary.
	 * @param appendExtension
	 *            ".exe" is appended for windows when searching the PATH.
	 * @param searchLocations
	 *            Common locations to search.
	 * @param filter
	 * 			File filter
	 * @return
	 */
	public static IPath find(String executableName, boolean appendExtension, List<IPath> searchLocations, FileFilter filter)
	{
		Map<String, String> env = ShellExecutable.getEnvironment();
		if (Platform.OS_WIN32.equals(Platform.getOS()))
		{
			String[] paths;
			if (env != null && env.containsKey("PATH")) { //$NON-NLS-1$
				paths = env.get("PATH").split(ShellExecutable.PATH_SEPARATOR); //$NON-NLS-1$
				for( int i = 0; i < paths.length; ++i) {
					if (paths[i].matches("^/(.)/.*")) { //$NON-NLS-1$
						paths[i] = paths[i].replaceFirst("^/(.)/", "$1:/"); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
			} else {
				String pathENV = System.getenv("PATH"); //$NON-NLS-1$
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
			String whichResult = ProcessUtil.outputForCommand("/usr/bin/which", null, env, executableName); //$NON-NLS-1$
			if (whichResult != null && whichResult.trim().length() > 0)
			{
				IPath whichPath = Path.fromOSString(whichResult.trim());
				if (isExecutable(whichPath) && (filter == null || filter.accept(whichPath.toFile())))
					return whichPath;
			}
		}

		// Still no path. Let's try some default locations.
		if (searchLocations != null)
		{
			for (IPath location : searchLocations)
			{
				IPath result = findExecutable(location.append(executableName), appendExtension);
				if (result != null && (filter == null || filter.accept(result.toFile())))
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
		}
		catch (Exception e)
		{
			// ignore, only available on Java 6+
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
