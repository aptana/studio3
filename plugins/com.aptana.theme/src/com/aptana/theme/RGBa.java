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
