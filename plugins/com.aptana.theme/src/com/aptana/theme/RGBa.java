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

	public RGBa(int red, int green, int blue)
	{
		this(red, green, blue, 255);
	}

	public RGBa(int red, int green, int blue, int alpha)
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

}
