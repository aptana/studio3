/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * @author Kevin Lindsey
 */
public class ColorManager implements ISharedTextColors
{
	// TODO Hook an auto dispose like ColorRegistry when display is disposed?
	protected Map<RGB, Color> _colorsByRGB = new HashMap<RGB, Color>(10);

	public void dispose()
	{
		for (Color c : this._colorsByRGB.values())
		{
			c.dispose();
		}
	}

	public Color getColor(RGB rgb)
	{
		Color color = this._colorsByRGB.get(rgb);
		if (color == null)
		{
			color = new Color(Display.getCurrent(), rgb);
			this._colorsByRGB.put(rgb, color);
		}
		return color;
	}
}
