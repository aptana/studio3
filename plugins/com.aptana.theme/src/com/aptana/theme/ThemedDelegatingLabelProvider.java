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

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * This label provider wraps another label provider and extends it to use them active theme's colors and font. This is
 * basically used to wrap label providers for the outline view.
 * 
 * @author cwilliams
 */
public class ThemedDelegatingLabelProvider implements ILabelProvider, IColorProvider, IFontProvider
{

	private ILabelProvider wrapped;
	private boolean disabled;

	public ThemedDelegatingLabelProvider(ILabelProvider wrapped)
	{
		this.wrapped = wrapped;
	}

	public void disable()
	{
		disabled = true;
	}

	public Image getImage(Object element)
	{
		return wrapped.getImage(element);
	}

	public String getText(Object element)
	{
		return wrapped.getText(element);
	}

	public Color getForeground(Object element)
	{
		if (disabled)
		{
			if (wrapped instanceof IColorProvider)
			{
				return ((IColorProvider) wrapped).getForeground(element);
			}
			return null;
		}
		return ThemePlugin.getDefault().getColorManager().getColor(getThemeManager().getCurrentTheme().getForeground());
	}

	protected IThemeManager getThemeManager()
	{
		return ThemePlugin.getDefault().getThemeManager();
	}

	public Color getBackground(Object element)
	{
		return null;
	}

	public Font getFont(Object element)
	{
		if (disabled)
		{
			if (wrapped instanceof IFontProvider)
			{
				return ((IFontProvider) wrapped).getFont(element);
			}
			return null;
		}
		Font font = JFaceResources.getFont(IThemeManager.VIEW_FONT_NAME);
		if (font == null)
		{
			font = JFaceResources.getTextFont();
		}
		return font;
	}

	public void addListener(ILabelProviderListener listener)
	{
		wrapped.addListener(listener);
	}

	public void dispose()
	{
		wrapped.dispose();
	}

	public boolean isLabelProperty(Object element, String property)
	{
		return wrapped.isLabelProperty(element, property);
	}

	public void removeListener(ILabelProviderListener listener)
	{
		wrapped.removeListener(listener);
	}

	public void enable()
	{
		disabled = false;
	}

}
