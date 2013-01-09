/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;

public class CSSColors
{
	/**
	 * Pattern used to verify hex color values.
	 */
	private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("^#[a-fA-F0-9]{3}([a-fA-F0-9]{3})?$"); //$NON-NLS-1$

	private static final String HASH = "#"; //$NON-NLS-1$
	private static Map<String, String> NAMED_COLORS = new HashMap<String, String>();
	static
	{
		// @formatter:off
		NAMED_COLORS = CollectionsUtil.newMap(
				"aqua", "#00FFFF", //$NON-NLS-1$ //$NON-NLS-2$
				"black", "#000000", //$NON-NLS-1$ //$NON-NLS-2$
				"blue", "#0000FF", //$NON-NLS-1$ //$NON-NLS-2$
				"fuchsia", "#FF00FF", //$NON-NLS-1$ //$NON-NLS-2$
				"gray", "#808080", //$NON-NLS-1$ //$NON-NLS-2$
				"green", "#008000", //$NON-NLS-1$ //$NON-NLS-2$
				"lime", "#00FF00", //$NON-NLS-1$ //$NON-NLS-2$
				"maroon", "#800000", //$NON-NLS-1$ //$NON-NLS-2$
				"navy", "#000080", //$NON-NLS-1$ //$NON-NLS-2$
				"olive", "#808000", //$NON-NLS-1$ //$NON-NLS-2$
				"purple", "#800080", //$NON-NLS-1$ //$NON-NLS-2$
				"red", "#FF0000", //$NON-NLS-1$ //$NON-NLS-2$
				"silver", "#C0C0C0", //$NON-NLS-1$ //$NON-NLS-2$
				"teal", "#008080", //$NON-NLS-1$ //$NON-NLS-2$
				"white", "#FFFFFF", //$NON-NLS-1$ //$NON-NLS-2$
				"yellow", "#FFFF00"); //$NON-NLS-1$ //$NON-NLS-2$
		// @formatter:on
	}

	public static boolean namedColorExists(String colorName)
	{
		return NAMED_COLORS.containsKey(colorName);
	}

	public static String getHexValueForName(String colorName)
	{
		return NAMED_COLORS.get(colorName);
	}

	public static Set<String> getNamedColors()
	{
		return Collections.unmodifiableSet(NAMED_COLORS.keySet());
	}

	public static String to6CharHexWithLeadingHash(String color)
	{
		if (namedColorExists(color.toLowerCase()))
		{
			return getHexValueForName(color.toLowerCase());
		}
		if (color.startsWith(HASH))
		{
			color = color.substring(1);
		}
		if (color.length() == 3)
		{
			return (HASH + color.charAt(0) + color.charAt(0) + color.charAt(1) + color.charAt(1) + color.charAt(2) + color
					.charAt(2)).toUpperCase();
		}
		return HASH + color.toUpperCase();
	}

	public static boolean isColor(String value)
	{
		if (StringUtil.isEmpty(value))
		{
			return false;
		}
		if (namedColorExists(value))
		{
			return true;
		}
		if (value.charAt(0) == '#' && (value.length() == 4 || value.length() == 7))
		{
			return HEX_COLOR_PATTERN.matcher(value).matches();
		}
		return false;
	}
}
