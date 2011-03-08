/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.File;

import org.eclipse.core.runtime.Path;

public class FileUtil
{
	/**
	 * The newline separator character
	 */
	public static String NEW_LINE = System.getProperty("line.separator"); //$NON-NLS-1$

	private FileUtil()
	{
	}

	public static boolean isDirectoryAccessible(File directory)
	{
		return ExecutableUtil.isExecutable(Path.fromOSString(directory.getAbsolutePath()));
	}

	/**
	 * Removes the "middle" part from a path to make it short enough to fit within the specified length, i.e.
	 * c:/Documents and Settings/username/My Documents/workspace/whatever.txt would become c:/Documents and
	 * Settings/.../workspace/whatever.txt.
	 * 
	 * @param path
	 *            the path to compress
	 * @param pathLength
	 *            the length to shorten it to. This is more of a guideline
	 * @return a compressed path
	 */
	public static String compressPath(String path, int pathLength)
	{
		path = path.replace('\\', '/');

		if (path.length() > pathLength)
		{
			// We want the second '/', as the path might begin with a '/'
			int firstSlash = path.indexOf('/', 1);
			int endSearch = path.length() - pathLength - firstSlash;
			if (firstSlash < 0 || endSearch < 0)
			{
				return path;
			}

			int lastSlash = path.indexOf('/', endSearch);
			if (lastSlash > firstSlash)
			{
				return path.substring(0, firstSlash) + "/..." + path.substring(lastSlash); //$NON-NLS-1$
			}

			// case where last segment is longer than the path length, but we could end with a '/'
			lastSlash = path.lastIndexOf('/', path.length() - 2);
			return path.substring(0, firstSlash) + "/..." + path.substring(lastSlash); //$NON-NLS-1$
		}
		return path;
	}

	/**
	 * Removes the "leading" part from a path to make it short enough to fit within the specified length, i.e.
	 * "c:/Documents and Settings/username/My Documents/workspace/whatever.txt" would become ".../My
	 * Documents/workspace/whatever.txt".
	 * 
	 * @param path
	 *            the path to compress
	 * @param pathLength
	 *            the length to shorten it to. This is more of a guideline
	 * @return a compressed path
	 */
	public static String compressLeadingPath(String path, int pathLength)
	{
		path = path.replace('\\', '/');

		if (path.length() <= pathLength)
		{
			return path;
		}

		int endSearch = path.length() - pathLength;
		int lastSlash = path.indexOf('/', endSearch);
		if (lastSlash < 0)
		{
			return path;
		}
		return "..." + path.substring(lastSlash); //$NON-NLS-1$
	}

	/**
	 * Get the extension.
	 * 
	 * @param fileName
	 *            File name
	 * @return the extension
	 */
	public static String getExtension(String fileName)
	{
		// We need kernel api to validate the extension or a filename
		if (fileName == null || StringUtil.EMPTY.equals(fileName))
		{
			return fileName;
		}

		int index = fileName.lastIndexOf('.');
		if (index == -1)
		{
			return StringUtil.EMPTY;
		}
		if (index == fileName.length())
		{
			return StringUtil.EMPTY;
		}
		return fileName.substring(index + 1, fileName.length());
	}

	/**
	 * Creates a file name with a random integer number inserted between the prefix and suffix
	 * 
	 * @param prefix
	 *            the name of the file (sans extension)
	 * @param suffix
	 *            the extension of the file (including the '.')
	 * @return a new file name like test12534.txt
	 */
	public static String getRandomFileName(String prefix, String suffix)
	{
		if (suffix == null)
		{
			return prefix + (long) (Integer.MAX_VALUE * Math.random());
		}
		return prefix + (long) (Integer.MAX_VALUE * Math.random()) + suffix;
	}

	/**
	 * Deletes a file recursively. If it's a directory we delete depth first, then delete the directory. The result is
	 * true only if the directory and all it's children are deleted.
	 * 
	 * @param dir
	 * @return
	 */
	public static boolean deleteRecursively(File dir)
	{
		if (dir == null)
		{
			return false;
		}
		boolean result = true;
		if (dir.isDirectory())
		{
			for (File child : dir.listFiles())
			{
				result = result && deleteRecursively(child);
			}
		}
		return result && dir.delete();
	}

}
