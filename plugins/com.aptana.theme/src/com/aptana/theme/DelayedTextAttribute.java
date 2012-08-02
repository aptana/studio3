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

	public final RGBa foreground;
	public final RGBa background;
	public final int style;

	public DelayedTextAttribute(RGBa foreground, RGBa background, int style)
	{
		this.foreground = foreground;
		this.background = background;
		this.style = style;
	}

	public DelayedTextAttribute(RGBa foreground)
	{
		this(foreground, null, SWT.NORMAL);
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Foreground: ").append(foreground); //$NON-NLS-1$
		if (background != null)
		{
			builder.append(", Background: ").append(background); //$NON-NLS-1$
		}
		builder.append(", Style: ").append(style); //$NON-NLS-1$
		return builder.toString();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof DelayedTextAttribute))
		{
			return false;
		}
		DelayedTextAttribute other = (DelayedTextAttribute) obj;
		return toString().equals(other.toString());
	}

	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}

}
