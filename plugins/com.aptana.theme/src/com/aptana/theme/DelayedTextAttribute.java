/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
