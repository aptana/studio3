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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.provider.FileSystem;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.aptana.core.CorePlugin;
import com.aptana.core.logging.IdeLog;

public class FileUtil
{
	/**
	 * The newline separator character
	 */
	public static String NEW_LINE = System.getProperty("line.separator"); //$NON-NLS-1$

	private FileUtil()
	{
	}

	public static IPath getTempDirectory()
	{
		return Path.fromOSString(System.getProperty("java.io.tmpdir")); //$NON-NLS-1$
	}

	public static boolean isDirectoryAccessible(File directory)
	{
		if (directory == null || !directory.isDirectory())
		{
			return false;
		}
		return directory.list() != null;
	}

	/**
	 * Returns true if the given file is symlink
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static boolean isSymlink(File file) throws IOException
	{
		Assert.isLegal(file != null);
		File canonical;
		if (file.getParent() == null)
		{
			canonical = file;
		}
		else
		{
			File canonicalDir = file.getParentFile().getCanonicalFile();
			canonical = new File(canonicalDir, file.getName());
		}
		return !canonical.getCanonicalFile().equals(canonical.getAbsoluteFile());
	}

	/**
	 * Removes the "middle" part from a path to make it short enough to fit within the specified length, i.e.
	 * c:/Documents and Settings/username/My Documents/workspace/whatever.txt would become c:/.../username/My
	 * Documents/workspace/whatever.txt.
	 * 
	 * @param path
	 *            the path to compress
	 * @param pathLength
	 *            the length to shorten it to. This is more of a guideline
	 * @return a compressed path
	 */
	public static String compressPath(String path, int pathLength)
	{
		if (StringUtil.isEmpty(path))
		{
			return path;
		}
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
			if (lastSlash > firstSlash)
			{
				return path.substring(0, firstSlash) + "/..." + path.substring(lastSlash); //$NON-NLS-1$
			}
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
		if (StringUtil.isEmpty(path))
		{
			return path;
		}
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
		if (StringUtil.isEmpty(fileName))
		{
			return fileName;
		}

		int index = fileName.lastIndexOf('.');
		if (index == -1 || index == fileName.length())
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
		StringBuilder name = new StringBuilder();
		if (prefix != null)
		{
			name.append(prefix);
		}
		name.append((long) (Integer.MAX_VALUE * Math.random()));
		if (suffix != null)
		{
			name.append(suffix);
		}
		return name.toString();
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

	/**
	 * Comb through the list of command-line arguments, and pull out the items that are files
	 * 
	 * @param arguments
	 * @return
	 */
	public static List<File> gatherFilesFromCommandLineArguments(String[] arguments)
	{
		List<File> files = new ArrayList<File>();
		for (int i = 0; i < arguments.length; i++)
		{
			// skip the keyring argument as a possible file
			if ("-keyring".equalsIgnoreCase(arguments[i])) { //$NON-NLS-1$
				if (arguments.length > i + 1)
				{
					i++; // skip the argument for the actual file
				}
			}
			else
			{
				File file = new File(arguments[i]);
				if (file.exists())
				{
					files.add(file);
				}
			}
		}

		return files;
	}

	/**
	 * Given a directory (or file), we recursively count the number of files in the directory tree. If the argument is
	 * null, returns 0. If a file is a symlink, it is counted as 1 and is not followed. Directories are not counted, but
	 * any files underneath them are.
	 * 
	 * @param file
	 * @return
	 */
	public static int countFiles(File file)
	{
		if (file == null)
		{
			return 0;
		}

		try
		{
			if (isSymlink(file))
			{
				return 1;
			}

			if (file.isDirectory())
			{
				int sum = 0;
				File[] children = file.listFiles();
				for (File child : children)
				{
					sum += countFiles(child);
				}
				return sum;
			}
		}
		catch (IOException e)
		{
			IdeLog.logError(CorePlugin.getDefault(), e);
		}
		return 1;
	}

	/**
	 * A simple check that the directory path is a valid one for the current OS. The check does not test for existence
	 * or write permissions, just for the path structure by using {@link File#getCanonicalPath()}.
	 * 
	 * @param path
	 * @return <code>true</code> if the given path is a valid one; <code>false</code> otherwise.
	 */
	public static boolean isValidDirectory(String path)
	{
		File file = new File(path);
		try
		{
			file.getCanonicalPath();
			return true;
		}
		catch (IOException e)
		{
			return false;
		}
	}
}
