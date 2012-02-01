/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.views;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.html.HTMLPlugin;
import com.aptana.editor.html.contentassist.model.BaseElement;
import com.aptana.editor.html.contentassist.model.CSSReference;
import com.aptana.editor.html.contentassist.model.CSSReferencesGroup;
import com.aptana.editor.html.contentassist.model.HTMLElement;
import com.aptana.editor.html.contentassist.model.JSReference;
import com.aptana.editor.html.contentassist.model.JSReferencesGroup;

public class HTMLIndexViewLabelProvider implements ILabelProvider
{
	private static final Image ATTRIBUTE_ICON = HTMLPlugin.getImage("icons/attribute.png"); //$NON-NLS-1$
	private static final Image CSS_ICON = HTMLPlugin.getImage("icons/css.png"); //$NON-NLS-1$
	private static final Image FOLDER_ICON = HTMLPlugin.getImage("icons/folder.png"); //$NON-NLS-1$
	private static final Image HTML_ICON = HTMLPlugin.getImage("icons/html.png"); //$NON-NLS-1$
	private static final Image JS_ICON = HTMLPlugin.getImage("icons/js.png"); //$NON-NLS-1$

	public void addListener(ILabelProviderListener listener)
	{
	}

	public void dispose()
	{
	}

	public Image getImage(Object element)
	{
		Image result = null;

		if (element instanceof HTMLElement)
		{
			result = HTML_ICON;
		}
		else if (element instanceof CSSReferencesGroup || element instanceof JSReferencesGroup)
		{
			result = FOLDER_ICON;
		}
		else if (element instanceof CSSReference)
		{
			result = CSS_ICON;
		}
		else if (element instanceof JSReference)
		{
			result = JS_ICON;
		}
		else if (element instanceof String)
		{
			result = ATTRIBUTE_ICON;
		}

		return result;
	}

	public String getText(Object element)
	{
		String result = null;

		if (element instanceof BaseElement)
		{
			result = ((BaseElement<?>) element).getName();
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
