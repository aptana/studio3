/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
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
 * @author Fabio Zadrozny
 */
public class ColorManager implements ISharedTextColors
{
	protected final Map<RGB, Color> _colorsByRGB = new HashMap<RGB, Color>(7);

	private final Object lock = new Object();

	public void dispose()
	{
		synchronized (lock)
		{
			for (Color c : _colorsByRGB.values())
			{
				c.dispose();
			}
			_colorsByRGB.clear();
		}
	}

	public Color getColor(final RGB rgb)
	{
		Color color;
		synchronized (lock)
		{
			color = this._colorsByRGB.get(rgb);
		}

		if (color == null)
		{
			// Note: creating the color will obtain a Lock to the UI thread (so, if it's currently
			// running, it'll only really happen after the UI thread unlocks it). So, to avoid the deadlock
			// described at APSTUD-7392, we have to do this without a lock (and re-lock it afterwards
			// to access our cache).
			Color newColor = new Color(Display.getCurrent(), rgb);
			synchronized (lock)
			{
				// Must check the cache again (it may've been created in the meanwhile).
				color = this._colorsByRGB.get(rgb);
				if (color == null)
				{
					// Was not created: put the new color in the cache and don't set it to null
					// so that it's not disposed.
					color = newColor;
					newColor = null;
					this._colorsByRGB.put(rgb, color);
				}
			}
			if (newColor != null)
			{
				newColor.dispose();
			}
		}
		return color;
	}
}
