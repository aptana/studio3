/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.Version;

import com.aptana.core.CorePlugin;
import com.aptana.core.logging.IdeLog;

/**
 * @author Max Stepanov
 */
public final class VersionUtil
{
	// Match x.y and x.y.z
	private static final String VERSION_SPLIT_PATTERN = "(\\d+)\\.(\\d+)(\\.(\\d+))?"; //$NON-NLS-1$
	// Match any dot separated string
	private static Pattern VERSION_DOT_PATTERN = Pattern.compile("\\."); //$NON-NLS-1$

	/**
	 * 
	 */
	private VersionUtil()
	{
	}

	/**
	 * Compare version strings of the form A.B.C.D... Version strings can contain integers or strings. It will attempt
	 * to compare individual '.'-delineated segments using an integer-based comparison first, and then will fall back to
	 * strings if the integer comparison fails.
	 * 
	 * @param left
	 * @param right
	 * @return positive if left > right, zero if left == right, negative otherwise
	 */
	public static int compareVersions(String left, String right)
	{
		int result;
		String[] lparts = VERSION_DOT_PATTERN.split(left);
		String[] rparts = VERSION_DOT_PATTERN.split(right);
		for (int i = 0; i < lparts.length && i < rparts.length; ++i)
		{
			try
			{
				Integer lInt = Integer.valueOf(lparts[i]);
				Integer rInt = Integer.valueOf(rparts[i]);
				result = lInt.compareTo(rInt);
			}
			catch (NumberFormatException ex)
			{
				result = lparts[i].compareToIgnoreCase(rparts[i]);
			}

			if (result != 0)
			{
				return result;
			}
		}
		return (lparts.length - rparts.length);
	}

	/**
	 * Parse the raw output and return a {@link Version} instance out of it.
	 * 
	 * @param rawOutput
	 * @return A {@link Version} instance. Null if the output did not contain a parsable version number.
	 */
	public static Version parseVersion(String rawOutput)
	{
		Pattern pattern = Pattern.compile(VERSION_SPLIT_PATTERN);
		Matcher matcher = pattern.matcher(rawOutput);
		if (matcher.find())
		{
			String version = matcher.group();
			try
			{
				return Version.parseVersion(version);
			}
			catch (IllegalArgumentException iae)
			{
				// Should never happen, since the matcher found it. But just in case.
				IdeLog.logError(CorePlugin.getDefault(), "Error parsing the version string - " + version, iae); //$NON-NLS-1$
			}
		}
		return null;
	}

}
