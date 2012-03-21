/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aptana.core.util.IOUtil;

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
			InputStreamReader fr = null;
			BufferedReader reader = null;

			try
			{
				fr = new InputStreamReader(new FileInputStream(bundleFile), IOUtil.UTF_8);
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
