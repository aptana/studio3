/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.usage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kevin Lindsey
 */
public class FeatureInfo implements Comparable<FeatureInfo>
{

	private static final Pattern TRAILING_DOTTED_NUMBERS = Pattern.compile("(?:\\.[0-9]+)+$"); //$NON-NLS-1$

	public final String name;
	public final String version;
	public final boolean enabled;

	/**
	 * FeatureInfo
	 * 
	 * @param name
	 * @param version
	 * @param enabled
	 */
	public FeatureInfo(String name, String version, boolean enabled)
	{
		if (name != null)
		{
			// adjust name if it's an AJAX library
			Matcher m = TRAILING_DOTTED_NUMBERS.matcher(name);

			// remove any trailing dotted numbers (for AJAX libs)
			if (m.find())
			{
				name = name.substring(0, m.start());
			}
		}

		this.name = (name != null) ? name : ""; //$NON-NLS-1$
		this.version = (version != null) ? version : ""; //$NON-NLS-1$
		this.enabled = enabled;
	}

	public int compareTo(FeatureInfo o)
	{
		int result = name.compareTo(o.name);
		if (result == 0)
		{
			result = version.compareTo(o.version);
			if (result == 0)
			{
				if (enabled != o.enabled)
				{
					return enabled ? -1 : 1;
				}
			}
		}

		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof FeatureInfo))
		{
			return false;
		}
		FeatureInfo info = (FeatureInfo) obj;
		return name.equals(info.name) && version.equals(info.version) && enabled == info.enabled;
	}

	@Override
	public int hashCode()
	{
		int hash = name.hashCode();
		hash = 31 * hash + version.hashCode();
		hash = 31 * hash + (enabled ? 1 : 0);
		return hash;
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		text.append(name).append(":").append(version).append(":").append(enabled); //$NON-NLS-1$ //$NON-NLS-2$
		return text.toString();
	}
}
