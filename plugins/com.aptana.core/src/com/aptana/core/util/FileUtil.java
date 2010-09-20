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

public class FileUtil
{
	/**
	 * The newline separator character
	 */
	public static String NEW_LINE = System.getProperty("line.separator"); //$NON-NLS-1$

	private FileUtil()
	{
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
		else
		{
			return prefix + (long) (Integer.MAX_VALUE * Math.random()) + suffix;
		}
	}

}
