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
 * with certain Eclipse Public Licensed code and certain additional terms
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
