/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.views;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.contentassist.JSModelFormatter;
import com.aptana.editor.js.contentassist.model.BaseElement;
import com.aptana.editor.js.contentassist.model.ClassElement;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;

/**
 * JSIndexViewLabelProvider
 */
public class JSIndexViewLabelProvider implements ILabelProvider
{
	private static final Image CLASS_ICON = JSPlugin.getImage("icons/class.png"); //$NON-NLS-1$
	private static final Image CONSTRUCTOR_ICON = JSPlugin.getImage("icons/constructor.png"); //$NON-NLS-1$
	private static final Image FUNCTION_ICON = JSPlugin.getImage("icons/js_function.png"); //$NON-NLS-1$
	private static final Image PROPERTY_ICON = JSPlugin.getImage("icons/js_property.png"); //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener)
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element)
	{
		Image result = null;

		if (element instanceof ClassElement)
		{
			result = CLASS_ICON;
		}
		else if (element instanceof FunctionElement)
		{
			FunctionElement function = (FunctionElement) element;

			// Functions that have the same name as their types are constructors
			if (function.getName().equals(function.getOwningType()))
			{
				result = CONSTRUCTOR_ICON;
			}
			else
			{
				result = FUNCTION_ICON;
			}
		}
		else if (element instanceof PropertyElement)
		{
			// Use same logic we use in CA to determine icon type
			result = JSModelFormatter.getImage((PropertyElement) element);
		}
		else
		{
			// default to property (or return null?)
			result = PROPERTY_ICON;
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element)
	{
		String result = null;

		if (element instanceof FunctionElement)
		{
			FunctionElement function = (FunctionElement) element;

			result = JSModelFormatter.getSimpleDescription(function);
		}
		else if (element instanceof BaseElement)
		{
			result = ((BaseElement) element).getName();
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener)
	{
	}

}
