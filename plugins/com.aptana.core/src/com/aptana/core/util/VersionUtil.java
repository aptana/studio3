/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.osgi.service.resolver.VersionRange;
import org.osgi.framework.Version;

import com.aptana.core.CorePlugin;
import com.aptana.core.logging.IdeLog;

/**
 * @author Max Stepanov
 */
public final class VersionUtil
{
	// Match x.y and x.y.z
	private static final String VERSION_SPLIT_PATTERN = "(\\d+)\\.(\\d+)(([a-zA-Z0-9_\\-]+)|(\\.(\\d+)(\\.?[a-zA-Z0-9_\\-]+)?))?"; //$NON-NLS-1$
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
		return compareVersions(left, right, true);
	}

	/**
	 * Compare version strings of the form A.B.C.D... Version strings can contain integers or strings. It will attempt
	 * to compare individual '.'-delineated segments using an integer-based comparison first, and then will fall back to
	 * strings if the integer comparison fails.
	 * 
	 * @param left
	 * @param right
	 * @param isStrict
	 *            Specifies whether the versions should be formatted as "x.x.x" prior to the comparision
	 * @return positive if left > right, zero if left == right, negative otherwise
	 */
	public static int compareVersions(String left, String right, boolean isStrict)
	{
		return compareVersions(left, right, isStrict, false);
	}

	/**
	 * Compare version strings of the form A.B.C.D... Version strings can contain integers or strings. It will attempt
	 * to compare individual '.'-delineated segments using an integer-based comparison first, and then will fall back to
	 * strings if the integer comparison fails.
	 * 
	 * @param left
	 * @param right
	 * @param isStrict
	 *            Specifies whether the versions should be formatted as "x.x.x" prior to the comparision
	 * @param handleHyphen
	 *            Specifies whether it handle or ignore hyphen in the micro version identifier.
	 * @return positive if left > right, zero if left == right, negative otherwise
	 */
	public static int compareVersions(String left, String right, boolean isStrict, boolean handleHyphen)
	{
		if (left == null)
		{
			left = StringUtil.EMPTY;
		}
		if (right == null)
		{
			right = StringUtil.EMPTY;
		}
		int result;
		String[] lparts = VERSION_DOT_PATTERN.split(left);
		String[] rparts = VERSION_DOT_PATTERN.split(right);

		// Make versions equal length
		if (!isStrict && lparts.length != rparts.length)
		{
			int diff = Math.abs(lparts.length - rparts.length);
			String[] moreParts = new String[diff];
			for (int i = 0; i < moreParts.length; i++)
			{
				moreParts[i] = "0"; //$NON-NLS-1$
			}

			if (lparts.length < rparts.length)
			{
				lparts = ArrayUtil.flatten(lparts, moreParts);
			}
			else
			{
				rparts = ArrayUtil.flatten(rparts, moreParts);
			}
		}

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
				if (handleHyphen)
				{
					result = compareVersionsWithHyphen(lparts[i], rparts[i]);
				}
				else
				{
					result = lparts[i].compareToIgnoreCase(rparts[i]);
				}
			}

			if (result != 0)
			{
				return result;
			}
		}
		return (lparts.length - rparts.length);
	}

	/**
	 * Compares the identifiers with hyphen in version similar to '3.0.1-cr' with the rule that the release candidate
	 * version '3.0.1-cr' is always less than GA version '3.0.1'.
	 * 
	 * @param left
	 * @param right
	 * @return
	 */
	private static int compareVersionsWithHyphen(String left, String right)
	{
		boolean hasLeftHyphen = false, hasRightHyphen = false;
		int hyphenIndex = left.indexOf('-');
		if (hyphenIndex > -1)
		{
			hasLeftHyphen = true;
			left = left.substring(0, hyphenIndex);
		}
		hyphenIndex = right.indexOf('-');
		if (hyphenIndex > -1)
		{
			hasRightHyphen = true;
			right = right.substring(0, hyphenIndex);
		}
		// No need to check based on hyphen in version identifier if either both or none of them have hyphen.
		if (left.equals(right) && hasLeftHyphen != hasRightHyphen)
		{
			if (hasLeftHyphen) // 1-cr < 1
			{
				return -1;
			}
			else if (hasRightHyphen) // 1 > 1-cr
			{
				return 1;
			}
		}
		return left.compareTo(right);
	}

	/**
	 * Parse the raw output and return a {@link Version} instance out of it.
	 * 
	 * @param rawOutput
	 * @return A {@link Version} instance. Null if the output did not contain a parseable version number.
	 */
	public static Version parseVersion(String rawOutput)
	{
		Pattern pattern = Pattern.compile(VERSION_SPLIT_PATTERN);
		Matcher matcher = pattern.matcher(rawOutput);
		if (matcher.find())
		{
			String major = matcher.group(1);
			String minor = matcher.group(2);
			String micro;
			String qualifier;
			if (matcher.group(5) != null)
			{
				// We have 3 parts with an optional qualifier
				micro = matcher.group(6);
				qualifier = matcher.group(7);
			}
			else
			{ // We have a major and minor with optional qualifier
				qualifier = matcher.group(4);
				micro = "0"; //$NON-NLS-1$
			}
			String version = major + '.' + minor + '.' + micro;
			if (!StringUtil.isEmpty(qualifier))
			{
				char c = qualifier.charAt(0);
				switch (c)
				{
					case '-':
					case '_':
					case '.':
						qualifier = qualifier.substring(1);
						break;

					default:
						break;
				}
				version = version + '.' + qualifier;
			}
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

	/**
	 * Check if the installed versions falls in the required versions. The strings in the required versions may include
	 * an 'inclusive'/'exclusive' ranges in a form of [a,b), [a,b] or (a,b].<br>
	 * The compatibility check will match an exact version when the required version appears as-is (e.g. <i>"1.0"</i>),
	 * and will perform a range check when the requirement appears as a range (e.g. <i>"[1.0, 2.3)"</i>).<br>
	 * <b>Every</b> item in the <i>required</i> list of versions have to have a match in the <i>installed</i> versions
	 * in order to have this method return <code>true</code>.
	 * 
	 * @param installedVersions
	 *            An array of installed versions.
	 * @param requiredVersions
	 *            An array of required versions. Each item can represent a version, or a range of versions (e.g. "a",
	 *            "a.b", "a.b.c", "[a, b)", "[a.b, c.e]" etc).
	 * @return <code>true</code> in case the installed versions match the required versions.
	 */
	public static boolean isCompatibleVersions(String[] installedVersions, String[] requiredVersions)
	{
		if (installedVersions == null || requiredVersions == null)
		{
			return requiredVersions != null && requiredVersions.length > 0;
		}
		// Hold the installed versions as Version instances
		Map<String, Version> installed = new HashMap<String, Version>();
		for (String installedVer : installedVersions)
		{
			try
			{
				Version version = getVersion(installedVer);
				if (version != null)
				{
					installed.put(installedVer, version);
				}
				else
				{
					installed.put(installedVer, Version.emptyVersion);
				}
			}
			catch (Exception e)
			{
				IdeLog.logWarning(CorePlugin.getDefault(),
						MessageFormat.format("Error parsing the installed version {0}", installedVer)); //$NON-NLS-1$
			}
		}
		// We need to match every version/version-range in the required versions array.
		for (String required : requiredVersions)
		{
			// We check if the required version starts with '[' or '('. If so, we treat it as a range of versions and
			// create a VersionRange instance for it. Otherwise, we treat it as a regex pattern.
			// Note that the JSON content will have to pass a valid syntax for the Java Pattern class. In any other
			// case, we log an error and ignore it.
			if (isRange(required))
			{
				if (!isCompatibleVersionsRange(installed, required))
				{
					return false;
				}
			}
			else if (!isCompatibleVersionsRegex(installed, required))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns <code>true</code> in case the given version is in the given version range.
	 * 
	 * @param version
	 * @param versionRange
	 * @return <code>true</code> if the given version match the version range.
	 */
	public static boolean isCompatibleVersions(String version, String versionRange)
	{
		return isCompatibleVersions(new String[] { version }, new String[] { versionRange });
	}

	private static boolean isCompatibleVersionsRegex(Map<String, Version> installed, String required)
	{
		// compile the regex.
		Pattern pattern = null;
		try
		{
			pattern = Pattern.compile(required);
		}
		catch (PatternSyntaxException e)
		{
			IdeLog.logError(CorePlugin.getDefault(), MessageFormat.format(
					"The required version '{0}' should be defined as a regular-expression", required)); //$NON-NLS-1$
			return false;
		}

		// Do a match on the installed-versions original String values
		for (String installedVersion : installed.keySet())
		{
			Matcher matcher = pattern.matcher(installedVersion);
			if (matcher.find())
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * @param installed
	 * @param matchCount
	 * @param required
	 * @return
	 */
	private static boolean isCompatibleVersionsRange(Map<String, Version> installed, String required)
	{
		VersionRange versionRange = new VersionRange(required);
		for (Version installedVersion : installed.values())
		{
			if (versionRange.isIncluded(installedVersion))
			{
				// Found a match for the requirement
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if the given version string represents a range of versions. For example, "[a, b]" or "[a, b)"...
	 * 
	 * @param versionString
	 * @return True, if the given string is a range representation.
	 */
	protected static boolean isRange(String versionString)
	{
		versionString = versionString.trim();
		if (versionString.charAt(0) == '[' || versionString.charAt(0) == '(')
		{
			int comma = versionString.indexOf(',');
			if (comma < 0)
			{
				return false;
			}
			char last = versionString.charAt(versionString.length() - 1);
			if (last != ']' && last != ')')
			{
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * Extract a Version out of a given version string. We are looking for a pattern that will match a version in a form
	 * of a.b.c (or less).
	 * 
	 * @param installedVer
	 * @return The 'synthesized' version of the given version string; <code>null</code> if no version was detected.
	 */
	public static Version getVersion(String version)
	{
		return VersionUtil.parseVersion(version);
	}

}
