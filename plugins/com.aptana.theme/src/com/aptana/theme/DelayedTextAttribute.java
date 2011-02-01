/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme;

import org.eclipse.swt.SWT;

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
		this(foreground, null, SWT.NORMAL);
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
		builder.append("Foreground: ").append(getForeground()); //$NON-NLS-1$
		if (getBackground() != null)
		{
			builder.append(", Background: ").append(getBackground()); //$NON-NLS-1$
		}
		builder.append(", Style: ").append(getStyle()); //$NON-NLS-1$
		return builder.toString();
	}

}
