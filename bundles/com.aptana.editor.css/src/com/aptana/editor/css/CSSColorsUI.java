/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import com.aptana.css.core.CSSColors;

public class CSSColorsUI
{
	private static final String HASH = "#"; //$NON-NLS-1$

	public static RGB hexToRGB(String color)
	{
		return toRGB(CSSColors.to6CharHexWithLeadingHash(color));
	}

	public static RGB namedColorToRGB(String name)
	{
		return toRGB(CSSColors.getHexValueForName(name));
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
		if (CSSColors.namedColorExists(color.toLowerCase()))
		{
			color = CSSColors.getHexValueForName(color.toLowerCase());
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

	public static Image toImage(String color, int height, int width)
	{
		RGB actualColor = toRGB(color);
		PaletteData paletteData = new PaletteData(new RGB[] { actualColor, new RGB(0, 0, 0) });
		ImageData imageData = new ImageData(16, 16, 1, paletteData);
		return new Image(Display.getDefault(), imageData);
	}
}
