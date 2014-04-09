/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme;

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

	public ThemedDelegatingLabelProvider(ILabelProvider wrapped)
	{
		this.wrapped = wrapped;
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
		if (wrapped instanceof IColorProvider)
		{
			return ((IColorProvider) wrapped).getForeground(element);
		}
		return null;
	}

	public Color getBackground(Object element)
	{
		return null;
	}

	public Font getFont(Object element)
	{
		return null;
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

}
