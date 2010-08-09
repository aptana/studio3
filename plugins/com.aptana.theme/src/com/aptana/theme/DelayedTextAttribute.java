package com.aptana.theme;

public class DelayedTextAttribute
{

	private RGBa foreground;
	private RGBa background;
	private int fontStyle;

	public DelayedTextAttribute(RGBa foreground, RGBa background, int style)
	{
		this.foreground = foreground;
		this.background = background;
		this.fontStyle = style;
	}

	public DelayedTextAttribute(RGBa foreground)
	{
		this(foreground, null, 0);
	}

	public RGBa getForeground()
	{
		return foreground;
	}

	public RGBa getBackground()
	{
		return background;
	}

	public int getStyle()
	{
		return fontStyle;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Foreground: ").append(getForeground());
		if (getBackground() != null)
		{
			builder.append(", Background: ").append(getBackground());
		}
		builder.append(", Style: ").append(getStyle());
		return builder.toString();
	}

}
