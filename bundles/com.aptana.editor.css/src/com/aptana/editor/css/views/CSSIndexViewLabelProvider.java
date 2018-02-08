/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.css.views;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.aptana.css.core.model.BaseElement;
import com.aptana.css.core.model.CSSElement;
import com.aptana.css.core.model.ClassGroupElement;
import com.aptana.css.core.model.ColorGroupElement;
import com.aptana.css.core.model.IdGroupElement;
import com.aptana.editor.css.CSSPlugin;

public class CSSIndexViewLabelProvider implements ILabelProvider
{
	private static final Image CSS_ICON = CSSPlugin.getImage("icons/css.png"); //$NON-NLS-1$
	private static final Image FOLDER_ICON = CSSPlugin.getImage("icons/folder.png"); //$NON-NLS-1$
	private static final Image PROPERTY_ICON = CSSPlugin.getImage("icons/property.png"); //$NON-NLS-1$

	public void addListener(ILabelProviderListener listener)
	{
	}

	public void dispose()
	{
	}

	public Image getImage(Object element)
	{
		Image result = null;

		if (element instanceof CSSElement)
		{
			result = CSS_ICON;
		}
		else if (element instanceof ClassGroupElement || element instanceof IdGroupElement
				|| element instanceof ColorGroupElement)
		{
			result = FOLDER_ICON;
		}
		else if (element instanceof String)
		{
			result = PROPERTY_ICON;
		}

		return result;
	}

	public String getText(Object element)
	{
		String result = null;

		if (element instanceof BaseElement)
		{
			result = ((BaseElement) element).getName();
		}
		else if (element instanceof String)
		{
			result = (String) element;
		}

		return result;
	}

	public boolean isLabelProperty(Object element, String property)
	{
		return false;
	}

	public void removeListener(ILabelProviderListener listener)
	{
	}
}
