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
	private static final String VERSION_SPLIT_PATTERN = "(\\d+)(\\.(\\d+)(([a-zA-Z0-9_\\-]+)|(\\.(\\d+)(\\.?[a-zA-Z0-9_\\-]+)?))?)?"; //$NON-NLS-1$
	/**
	 * This pattern will help to match the patterns related to ">=24 <=20", or ">24", or "<=20" and helps to parse the
	 * min or max version referenced in SDK configuration (package.json) files.
	 */
	private static final Pattern VERSION_RANGE_PATTERN = Pattern
			.compile("([>]?[=]?([0-9a-z.]+))?(\\s)*([<]?[=]?([0-9a-z.]+))?"); //$NON-NLS-1$

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
	static int compareVersionsWithHyphen(String left, String right)
	{
		boolean hasLeftHyphen = false, hasRightHyphen = false;
		int hyphenIndex = left.indexOf('-');
		String leftPreHyphen = left, leftPostHyphen = null;
		if (hyphenIndex > -1)
		{
			hasLeftHyphen = true;
			leftPreHyphen = left.substring(0, hyphenIndex);
			leftPostHyphen = left.substring(hyphenIndex + 1, left.length());
		}
		hyphenIndex = right.indexOf('-');
		String rightPreHyphen = right, rightPostHyphen = null;
		if (hyphenIndex > -1)
		{
			hasRightHyphen = true;
			rightPreHyphen = right.substring(0, hyphenIndex);
			rightPostHyphen = right.substring(hyphenIndex + 1, right.length());
		}
		// If both the version doesn't have hyphen, then just compare them.
		if (!hasLeftHyphen && !hasRightHyphen)
		{
			return left.compareTo(right);
		}
		// No need to check based on hyphen in version identifier if either both or none of them have hyphen.
		if (leftPreHyphen.equals(rightPreHyphen))
		{
			if (hasLeftHyphen != hasRightHyphen)
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
			else
			{
				return leftPostHyphen.compareTo(rightPostHyphen);
			}
		}
		return leftPreHyphen.compareTo(rightPreHyphen);
	}

	/**
	 * Parse the raw output and return a {@link Version} instance out of it.
	 * 
	 * @param rawOutput
	 * @return A {@link Version} instance. {@link Version#emptyVersion} if the output did not contain a parseable
	 *         version number.
	 */
	public static Version parseVersion(String rawOutput)
	{
		if (StringUtil.isEmpty(rawOutput))
		{
			return Version.emptyVersion;
		}
		Pattern pattern = Pattern.compile(VERSION_SPLIT_PATTERN);
		Matcher matcher = pattern.matcher(rawOutput);
		if (matcher.find())
		{
			String major = matcher.group(1);
			String minor = matcher.group(3);
			if (minor == null)
			{
				minor = "0"; //$NON-NLS-1$
			}
			String micro = "0"; //$NON-NLS-1$
			String qualifier;
			if (matcher.group(6) != null)
			{
				// We have 3 parts with an optional qualifier
				micro = matcher.group(7);
				qualifier = matcher.group(8);
			}
			else
			{ // We have a major and minor with optional qualifier
				qualifier = matcher.group(5);
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
			catch (Exception iae)
			{
				// Should never happen, since the matcher found it. But just in case.
				IdeLog.logError(CorePlugin.getDefault(), "Error parsing the version string - " + version, iae); //$NON-NLS-1$
			}
		}
		return Version.emptyVersion;
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
				Version version = parseVersion(installedVer);
				installed.put(installedVer, version);
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
	 * Returns true is version object is null or is {@link Version#emptyVersion}
	 * 
	 * @param version
	 * @return
	 */
	public static boolean isEmpty(Version version)
	{
		if (version == null)
		{
			return true;
		}
		if (version.equals(Version.emptyVersion))
		{
			return true;
		}
		return false;
	}

	/**
	 * Parse the version range with the format <code>">=20.0 <24.x", or ">24", or "<=20"</code> and returns the minimum
	 * required version from the range.
	 * 
	 * @param versionRange
	 * @return
	 */
	public static String parseMin(String versionRange)
	{
		if (StringUtil.isEmpty(versionRange))
		{
			return null;
		}
		Matcher matcher = VERSION_RANGE_PATTERN.matcher(versionRange);
		if (matcher.matches())
		{
			String minVersion = matcher.group(2);
			// The versions are sometimes tagged as 24.x, which has to be changed to 24.0 in order to parse without any
			// errors.
			if (!StringUtil.isEmpty(minVersion))
			{
				return minVersion.replace('x', '0');
			}
		}
		return null;
	}

	/**
	 * Parse the version range with the format <code>">=20.0 <24.x", or ">24", or "<=20"</code> and returns the maximum
	 * required version from the range.
	 * 
	 * @param versionRange
	 * @return
	 */
	public static String parseMax(String versionRange)
	{
		if (StringUtil.isEmpty(versionRange))
		{
			return null;
		}
		Matcher matcher = VERSION_RANGE_PATTERN.matcher(versionRange);
		if (matcher.matches())
		{
			String maxVersion = matcher.group(5);
			if (!StringUtil.isEmpty(maxVersion))
			{
				return maxVersion.replace('x', '0');
			}
		}
		return null;
	}
	
	/**
	 * This behaves same as {@link #isCompatibleVersions(String[] installedVersions, String[] requiredVersions)} when
	 * requiredVersions is a version range(ex: [5.0,9.0]), but behaves differently when it's not a range(ex: [5.0]). <br>
	 * When range is not specified it will consider required version(ex: 5.0) as a minimum required version. In this
	 * case, 5.0 is a minimum required version. <br>
	 * <b>Examples:</b> <br>
	 * <code>isMinimumCompatibleVersions(new String[]{ "9.0"},new String[]{ "[5.0]" }); </code> //true <br>
	 * <code>isMinimumCompatibleVersions(new String[]{ "5.0", "2.0" },new String[]{ "[5.0, 9.0]"}); </code> //true <br>
	 * <code>isMinimumCompatibleVersions(new String[]{ "8.4", "9.0"},new String[]{ "[5.0, 8.3]" }); </code> //false <br>
	 * <code>isMinimumCompatibleVersions(new String[]{ "7.0"},new String[]{ "[8.0]" }); </code> //false <br>
	 * 
	 * @param installedVersions
	 * @param requiredVersions
	 * @return
	 */
	public static boolean isMinimumCompatibleVersions(String[] installedVersions, String[] requiredVersions)
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
				Version version = parseVersion(installedVer);
				installed.put(installedVer, version);
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
			else if (!isMinimumCompatibleVersionsRegex(installed, required))
			{
				return false;
			}
		}
		return true;
	}
	
	private static boolean isMinimumCompatibleVersionsRegex(Map<String, Version> installed, String required)
	{
		// compile the regex.
		try
		{
			Pattern.compile(required);
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
			Version minRequiredVersion = VersionUtil.parseVersion(required);
			if (installed.get(installedVersion).compareTo(minRequiredVersion) >= 0)
			{
				return true;
			}
		}
		return false;
	}
}
