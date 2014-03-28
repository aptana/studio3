/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme;

import org.eclipse.swt.graphics.RGB;

public class RGBa
{

	private int red;
	private int blue;
	private int green;
	private int alpha;

	public RGBa(RGB rgb)
	{
		this(rgb.red, rgb.green, rgb.blue);
	}

	RGBa(int red, int green, int blue)
	{
		this(red, green, blue, 255);
	}

	RGBa(int red, int green, int blue, int alpha)
	{
		this.red = red;
		this.blue = blue;
		this.green = green;
		this.alpha = alpha;
	}

	public int getAlpha()
	{
		return alpha;
	}

	public RGB toRGB()
	{
		return new RGB(red, green, blue);
	}

	@Override
	public String toString()
	{
		return "{" + red + ", " + green + ", " + blue + ", " + alpha + "}"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	}

	public boolean isFullyTransparent()
	{
		return alpha == 0;
	}

	public boolean isFullyOpaque()
	{
		return alpha == 255;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		if (obj instanceof RGB)
		{
			RGB other = (RGB) obj;
			return isFullyOpaque() && other.red == red && other.green == green && other.blue == blue;
		}
		if (obj instanceof RGBa)
		{
			RGBa other = (RGBa) obj;
			return other.alpha == alpha && other.red == red && other.green == green && other.blue == blue;
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return (alpha << 32) | (blue << 16) | (green << 8) | red;
	}

}
