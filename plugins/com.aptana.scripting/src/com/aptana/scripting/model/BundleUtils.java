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
package com.aptana.scripting.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BundleUtils
{
	public static final String LIB_DIRECTORY_NAME = "lib"; //$NON-NLS-1$

	private static final String BUNDLE_DIRECTORY_SUFFIX = ".ruble"; //$NON-NLS-1$
	private static final Pattern BUNDLE_NAME = Pattern.compile("^\\s*bundle\\s*(\"(?:\\\"|[^\"\\r\\n])*\"|'(?:\\\\'|[^'\\r\\n]*'))?(?:\\s*do|(?m:\\s*)\\{)"); //$NON-NLS-1$

	public static BundleUtils INSTANCE;

	/**
	 * BundleUtils
	 */
	private BundleUtils()
	{
	}

	/**
	 * getBundleLibDirectory
	 * 
	 * @param bundleDirectory
	 * @return
	 */
	public static String getBundleLibDirectory(File bundleDirectory)
	{
		return new File(bundleDirectory, LIB_DIRECTORY_NAME).getAbsolutePath();
	}

	/**
	 * getBundleName
	 * 
	 * @param bundleFile
	 * @return
	 */
	public static String getBundleName(File bundleFile)
	{
		String result = null;

		if (bundleFile.isFile() && bundleFile.canRead())
		{
			FileReader fr = null;
			BufferedReader reader = null;

			try
			{
				fr = new FileReader(bundleFile);
				reader = new BufferedReader(fr);
				String line;

				while ((line = reader.readLine()) != null)
				{
					Matcher m = BUNDLE_NAME.matcher(line);

					if (m.find())
					{
						result = m.group(1);

						if (result != null && result.length() >= 2)
						{
							result = result.substring(1, result.length() - 1);
						}

						break;
					}
				}
			}
			catch (FileNotFoundException e)
			{
			}
			catch (IOException e)
			{
			}
			finally
			{
				if (reader != null)
				{
					try
					{
						reader.close();
					}
					catch (IOException e)
					{
					}
				}
			}
		}

		if (result == null || result.length() == 0)
		{
			result = getDefaultBundleName(bundleFile.getAbsolutePath());
		}

		return result;
	}

	/**
	 * getDefaultBundleName
	 * 
	 * @param path
	 * @return
	 */
	public static String getDefaultBundleName(String path)
	{
		String result = null;

		if (path != null && path.length() > 0)
		{
			File file = new File(path).getParentFile();

			result = file.getName();

			if (result.endsWith(BUNDLE_DIRECTORY_SUFFIX))
			{
				result = result.substring(0, result.length() - BUNDLE_DIRECTORY_SUFFIX.length());
			}
		}

		return result;
	}
}
