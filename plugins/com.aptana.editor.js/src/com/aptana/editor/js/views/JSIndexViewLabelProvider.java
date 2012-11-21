/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.views;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.contentassist.JSModelFormatter;
import com.aptana.js.core.model.BaseElement;
import com.aptana.js.core.model.ClassElement;
import com.aptana.js.core.model.ClassGroupElement;
import com.aptana.js.core.model.EventElement;
import com.aptana.js.core.model.FunctionElement;
import com.aptana.js.core.model.JSElement;
import com.aptana.js.core.model.PropertyElement;

/**
 * JSIndexViewLabelProvider
 */
public class JSIndexViewLabelProvider implements ILabelProvider, ILabelDecorator
{

	private static final Image CLASS_ICON = JSPlugin.getImage("icons/class.png"); //$NON-NLS-1$
	private static final Image CONSTRUCTOR_ICON = JSPlugin.getImage("icons/constructor.png"); //$NON-NLS-1$
	private static final Image FUNCTION_ICON = JSPlugin.getImage("icons/js_function.png"); //$NON-NLS-1$
	private static final Image PROPERTY_ICON = JSPlugin.getImage("icons/js_property.png"); //$NON-NLS-1$
	private static final Image FOLDER_ICON = JSPlugin.getImage("icons/folder.png"); //$NON-NLS-1$
	private static final Image SCRIPT_ICON = JSPlugin.getImage("icons/js.png"); //$NON-NLS-1$
	private static final Image EVENT_ICON = JSPlugin.getImage("icons/event.gif"); //$NON-NLS-1$

	private static final ImageDescriptor STATIC_OVERLAY = JSPlugin.getImageDescriptor("icons/overlays/static.png"); //$NON-NLS-1$
	private static JSModelFormatter modelFormatter = JSModelFormatter.LABEL;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener)
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelDecorator#decorateImage(org.eclipse.swt.graphics.Image, java.lang.Object)
	 */
	public Image decorateImage(Image image, Object element)
	{
		Image result = null;

		if (image != null && element instanceof PropertyElement)
		{
			PropertyElement property = (PropertyElement) element;

			if (property.isClassProperty())
			{
				DecorationOverlayIcon decorator = new DecorationOverlayIcon(image, STATIC_OVERLAY,
						IDecoration.TOP_RIGHT);

				result = decorator.createImage();
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelDecorator#decorateText(java.lang.String, java.lang.Object)
	 */
	public String decorateText(String text, Object element)
	{
		return null;
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

		if (element instanceof JSElement)
		{
			result = SCRIPT_ICON;
		}
		else if (element instanceof ClassGroupElement)
		{
			result = FOLDER_ICON;
		}
		else if (element instanceof ClassElement)
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
			// Use same logic we use in CA to determine icon type. This gives us individual icons by property type
			result = modelFormatter.getImage((PropertyElement) element);
		}
		else if (element instanceof EventElement)
		{
			result = EVENT_ICON;
		}
		else if (element instanceof BaseElement)
		{
			// default to property (or return null?)
			result = PROPERTY_ICON;
		}

		Image decorated = this.decorateImage(result, element);

		if (decorated != null)
		{
			result = decorated;
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

			result = modelFormatter.getDescription(function, null);
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
