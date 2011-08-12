/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class CSSColors
{
	private static final String HASH = "#"; //$NON-NLS-1$
	private static Map<String, String> NAMED_COLORS = new HashMap<String, String>();
	static
	{
		NAMED_COLORS.put("aqua", "#00FFFF"); //$NON-NLS-1$ //$NON-NLS-2$
		NAMED_COLORS.put("black", "#000000"); //$NON-NLS-1$ //$NON-NLS-2$
		NAMED_COLORS.put("blue", "#0000FF"); //$NON-NLS-1$ //$NON-NLS-2$
		NAMED_COLORS.put("fuchsia", "#FF00FF"); //$NON-NLS-1$ //$NON-NLS-2$
		NAMED_COLORS.put("gray", "#808080"); //$NON-NLS-1$ //$NON-NLS-2$
		NAMED_COLORS.put("green", "#008000"); //$NON-NLS-1$ //$NON-NLS-2$
		NAMED_COLORS.put("lime", "#00FF00"); //$NON-NLS-1$ //$NON-NLS-2$
		NAMED_COLORS.put("maroon", "#800000"); //$NON-NLS-1$ //$NON-NLS-2$
		NAMED_COLORS.put("navy", "#000080"); //$NON-NLS-1$ //$NON-NLS-2$
		NAMED_COLORS.put("olive", "#808000"); //$NON-NLS-1$ //$NON-NLS-2$
		NAMED_COLORS.put("purple", "#800080"); //$NON-NLS-1$ //$NON-NLS-2$
		NAMED_COLORS.put("red", "#FF0000"); //$NON-NLS-1$ //$NON-NLS-2$
		NAMED_COLORS.put("silver", "#C0C0C0"); //$NON-NLS-1$ //$NON-NLS-2$
		NAMED_COLORS.put("teal", "#008080"); //$NON-NLS-1$ //$NON-NLS-2$
		NAMED_COLORS.put("white", "#FFFFFF"); //$NON-NLS-1$ //$NON-NLS-2$
		NAMED_COLORS.put("yellow", "#FFFF00"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static boolean namedColorExists(String colorName)
	{
		return NAMED_COLORS.containsKey(colorName);
	}

	private static String getHexValueForName(String colorName)
	{
		return NAMED_COLORS.get(colorName);
	}

	public static Set<String> getNamedColors()
	{
		return Collections.unmodifiableSet(NAMED_COLORS.keySet());
	}

	public static RGB hexToRGB(String color)
	{
		return toRGB(to6CharHexWithLeadingHash(color));
	}

	public static RGB namedColorToRGB(String name)
	{
		return toRGB(getHexValueForName(name));
	}

	/**
	 * Takes in a color name or 3/6 hex digit value (with optional leading hash). and Converts to an RGB object
	 * containing the corresponding color values.
	 * 
	 * @param color
	 * @return RGB
	 */
	private static RGB toRGB(String color)
	{
		if (namedColorExists(color.toLowerCase()))
		{
			color = getHexValueForName(color.toLowerCase());
		}
		if (color.startsWith(HASH))
		{
			color = color.substring(1);
		}
		if (color.length() == 3)
		{
			int red = Integer.decode(HASH + color.substring(0, 1) + color.substring(0, 1));
			int green = Integer.decode(HASH + color.substring(1, 2) + color.substring(1, 2));
			int blue = Integer.decode(HASH + color.substring(2, 3) + color.substring(2, 3));
			return new RGB(red, green, blue);
		}
		int red = Integer.decode(HASH + color.substring(0, 2));
		int green = Integer.decode(HASH + color.substring(2, 4));
		int blue = Integer.decode(HASH + color.substring(4, 6));
		return new RGB(red, green, blue);
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

	public static Image toImage(String color, int height, int width)
	{
		RGB actualColor = toRGB(color);
		PaletteData paletteData = new PaletteData(new RGB[] { actualColor, new RGB(0, 0, 0) });
		ImageData imageData = new ImageData(16, 16, 1, paletteData);
		return new Image(Display.getDefault(), imageData);
	}

}
