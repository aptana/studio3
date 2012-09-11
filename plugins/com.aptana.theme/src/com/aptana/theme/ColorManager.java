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

import com.aptana.ui.util.UIUtils;

/**
 * @author Kevin Lindsey
 * @author Fabio Zadrozny
 */
public class ColorManager implements ISharedTextColors
{
	protected final Map<RGB, Color> _colorsByRGB = new HashMap<RGB, Color>(7);

	public void dispose()
	{
		// Note: the dispose must always be run in the UI thread (note that we don't fail if this function
		// is not called from the UI-thread, we simply run it asynchronously later on).
		UIUtils.runInUIThread(new Runnable()
		{
			public void run()
			{
				for (Color c : _colorsByRGB.values())
				{
					c.dispose();
				}
				_colorsByRGB.clear();
			}
		});
	}

	/**
	 * @note this method must be called from the UI thread.
	 */
	public Color getColor(RGB rgb)
	{
		UIUtils.assertUIThread();
		Color color = this._colorsByRGB.get(rgb);
		if (color == null)
		{
			color = new Color(Display.getCurrent(), rgb);
			this._colorsByRGB.put(rgb, color);
		}
		return color;
	}
}
