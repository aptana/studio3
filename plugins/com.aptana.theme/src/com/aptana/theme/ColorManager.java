package com.aptana.theme;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * @author Kevin Lindsey
 */
public class ColorManager
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
