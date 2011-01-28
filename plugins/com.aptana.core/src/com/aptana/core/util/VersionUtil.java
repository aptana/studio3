/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.util.regex.Pattern;

/**
 * @author Max Stepanov
 */
public final class VersionUtil
{
	private static Pattern versionPattern = Pattern.compile("\\.");

	/**
	 * 
	 */
	private VersionUtil()
	{
	}

	/**
	 * Compare version strings of the form A.B.C.D... Version strings can contain integers or strings. It will attempt to compare
	 * individual '.'-delineated segments using an integer-based comparison first, and then will fall back to strings
	 * if the integer comparison fails.
	 * 
	 * @param left
	 * @param right
	 * @return positive if left > right, zero if left == right, negative otherwise
	 */
	public static int compareVersions(String left, String right)
	{
		int result;
		String[] lparts = versionPattern.split(left); //$NON-NLS-1$
		String[] rparts = versionPattern.split(right); //$NON-NLS-1$
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

}
