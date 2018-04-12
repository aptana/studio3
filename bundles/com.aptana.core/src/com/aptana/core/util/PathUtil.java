/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;

import com.aptana.core.ShellExecutable;

/**
 * A utility class intended to convert cygwin and MinGW POSIX paths to Windows ones.
 * 
 * @author Christopher Williams
 */
public class PathUtil
{

	private static final String PATHS_SEPERATOR = ":"; //$NON-NLS-1$
	private static final String UNIX_SEPERATOR = "/"; //$NON-NLS-1$
	private static final Pattern MINGW_PATH_REGEXP = Pattern.compile("^/(.)/(.*)"); //$NON-NLS-1$
	private static final Pattern CYGWWIN_PATH_REGEXP = Pattern.compile("^/cygdrive/(.)/(.*)"); //$NON-NLS-1$

	/**
	 * Converts windows based paths to unix format path. For instance, C:\Program Files (x86)\Java\jdk1.6.0_34\jre\bin
	 * is converted to /c/Program Files (x86)/Java/jdk1.6.0_34/jre/bin
	 */
	public static String convertToUnixFormatPath(String windowsPath)
	{
		String unixPath = windowsPath.replace(PATHS_SEPERATOR, StringUtil.EMPTY); // Remove colon from the path.
		unixPath = unixPath.replace('\\', '/');
		unixPath = StringUtil.concat(UNIX_SEPERATOR, unixPath, UNIX_SEPERATOR);
		return unixPath;
	}

	/**
	 * This method is intended to take the raw value of PATH and convert it to Windows format.
	 * 
	 * @param rawPATH
	 * @return
	 */
	public static String convertPATH(String rawPATH)
	{
		if (rawPATH == null || !PlatformUtil.isWindows())
		{
			return rawPATH;
		}
		// Handle if path didn't come from bash/mingw/cygwin, but from system env instead.
		if (rawPATH.indexOf(';') != -1)
		{
			return rawPATH;
		}

		// Cygwin - http://www.cygwin.com/cygwin-ug-net/using-utils.html
		if (rawPATH.contains("/cygdrive/")) //$NON-NLS-1$
		{
			try
			{
				String cygPathExe = ShellExecutable.getPath().removeLastSegments(1).append("cygpath.exe").toOSString(); //$NON-NLS-1$
				IStatus result = new ProcessRunner().runInBackground(cygPathExe, "-w", "-p", rawPATH); //$NON-NLS-1$ //$NON-NLS-2$
				if (result.isOK())
				{
					return result.getMessage();
				}
			}
			catch (CoreException e)
			{
				// ignore
			}
		}

		String[] paths = rawPATH.split(ShellExecutable.PATH_SEPARATOR);
		for (int i = 0; i < paths.length; ++i)
		{
			// MinGW
			// FIXME See http://www.mingw.org/wiki/Posix_path_conversion
			Matcher m = MINGW_PATH_REGEXP.matcher(paths[i]);
			if (m.matches())
			{
				paths[i] = m.replaceFirst("$1:/$2"); //$NON-NLS-1$
			}
			else
			{
				// Cygwin - fallback if cygpath failed
				m = CYGWWIN_PATH_REGEXP.matcher(paths[i]);
				if (m.matches())
				{
					paths[i] = m.replaceFirst("$1:/$2"); //$NON-NLS-1$
				}
			}
		}
		return StringUtil.join(File.pathSeparator, paths);
	}

}
