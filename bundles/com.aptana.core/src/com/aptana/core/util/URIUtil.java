/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

public class URIUtil
{

	/**
	 * URIUtil
	 */
	private URIUtil()
	{
	}

	/**
	 * decodeURI
	 * 
	 * @param uri
	 * @return
	 */
	public static String decodeURI(String uri)
	{
		String result = null;

		if (uri != null)
		{
			try
			{
				result = URLDecoder.decode(uri, "utf-8"); //$NON-NLS-1$
			}
			catch (UnsupportedEncodingException e)
			{
				// ignore, returns null
			}
		}

		return result;
	}

	/**
	 * Opens a file URI in a platform-specific way. Used for "Show in Finder" / "Show in Explorer"
	 * 
	 * @param uri
	 * @return
	 */
	public static boolean open(URI uri)
	{
		if (uri == null)
		{
			return false;
		}
		if (!"file".equalsIgnoreCase(uri.getScheme())) //$NON-NLS-1$
		{
			return false;
		}
		File file = new File(uri);
		if (Platform.getOS().equals(Platform.OS_MACOSX))
		{
			return openInFinder(file);
		}
		else if (Platform.getOS().equals(Platform.OS_WIN32))
		{
			return openInWindowsExplorer(file);
		}
		return openOnLinux(file);
	}

	private static boolean openOnLinux(File file)
	{
		// Can only handle directories
		if (file.isFile())
		{
			file = file.getParentFile();
		}

		// TODO Do we also need to try 'gnome-open' or 'dolphin' if nautilus fails?
		IStatus result = new ProcessRunner().runInBackground("nautilus", file.getAbsolutePath()); //$NON-NLS-1$
		if (result == null)
		{
			return false;
		}
		return result.isOK();
	}

	private static boolean openInWindowsExplorer(File file)
	{
		// This works for Windows XP Pro! Can't run under ProcessBuilder or it does some quoting/mangling of args that
		// breaks this!
		String explorer = PlatformUtil.expandEnvironmentStrings("%SystemRoot%\\explorer.exe"); //$NON-NLS-1$
		try
		{
			Process p = Runtime.getRuntime().exec("\"" + explorer + "\" /select,\"" + file.getAbsolutePath() + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			return p.exitValue() == 0;
		}
		catch (IOException e)
		{
			return false;
		}
	}

	private static boolean openInFinder(File file)
	{
		return openInFinder(file, file.isFile());
	}

	/**
	 * Opens a file in OSX Finder. Specifies whether the file should be opened or revealed
	 * 
	 * @param file
	 * @param reveal
	 * @return
	 */
	public static boolean openInFinder(File file, boolean reveal)
	{
		String path = file.getAbsolutePath();
		String subcommand = reveal ? "reveal" : "open"; //$NON-NLS-1$ //$NON-NLS-2$
		String appleScript = "tell application \"Finder\" to " + subcommand + " (POSIX file \"" + path + "\")\ntell application \"Finder\" to activate"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		IStatus result = new ProcessRunner().runInBackground("osascript", "-e", appleScript); //$NON-NLS-1$ //$NON-NLS-2$
		if (result != null && result.isOK())
		{
			return true;
		}
		// TODO Log output if failed?
		return false;
	}

	public static String getFileName(URI uri)
	{
		if (uri == null)
		{
			return null;
		}
		String uriPath = uri.getPath();
		IPath path = Path.fromPortableString(uriPath);
		if (path == null)
		{
			return null;
		}
		return path.lastSegment();
	}
}
