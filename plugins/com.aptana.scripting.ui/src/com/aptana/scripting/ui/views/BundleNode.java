/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.ui.views;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.ui.ScriptingUIPlugin;

class BundleNode extends BaseNode
{
	private enum Property
	{
		NAME, PATH, VISIBLE, REFERENCE, PRECEDENCE, AUTHOR, COPYRIGHT, DESCRIPTION, LICENSE, LICENSE_URL, REPOSITORY
	}

	private static final Image BUNDLE_ICON = ScriptingUIPlugin.getImage("icons/bundle_directory.png"); //$NON-NLS-1$
	private BundleElement _bundle;

	/**
	 * BundleNode
	 * 
	 * @param bundle
	 */
	public BundleNode(BundleElement bundle)
	{
		this._bundle = bundle;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getChildren()
	 */
	public Object[] getChildren()
	{
		CommandsNode commands = new CommandsNode(this._bundle);
		SnippetsNode snippets = new SnippetsNode(this._bundle);
		MenusNode menus = new MenusNode(this._bundle);
		List<Object> items = new LinkedList<Object>();

		if (commands.hasChildren())
		{
			items.add(commands);
		}
		if (snippets.hasChildren())
		{
			items.add(snippets);
		}
		if (menus.hasChildren())
		{
			items.add(menus);
		}

		return items.toArray(new Object[items.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getImage()
	 */
	public Image getImage()
	{
		return BUNDLE_ICON;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getLabel()
	 */
	public String getLabel()
	{
		File file = new File(this._bundle.getPath());

		return file.getAbsolutePath();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors()
	{
		PropertyDescriptor nameProperty = new PropertyDescriptor(Property.NAME, "Name"); //$NON-NLS-1$
		PropertyDescriptor pathProperty = new PropertyDescriptor(Property.PATH, "Path"); //$NON-NLS-1$
		PropertyDescriptor visibleProperty = new PropertyDescriptor(Property.VISIBLE, "Visible"); //$NON-NLS-1$
		PropertyDescriptor referenceProperty = new PropertyDescriptor(Property.REFERENCE, "Reference"); //$NON-NLS-1$
		PropertyDescriptor precedenceProperty = new PropertyDescriptor(Property.PRECEDENCE, "Precedence"); //$NON-NLS-1$
		PropertyDescriptor authorProperty = new PropertyDescriptor(Property.AUTHOR, "Author"); //$NON-NLS-1$
		PropertyDescriptor copyrightProperty = new PropertyDescriptor(Property.COPYRIGHT, "Copyright"); //$NON-NLS-1$
		PropertyDescriptor descriptionProperty = new PropertyDescriptor(Property.DESCRIPTION, "Description"); //$NON-NLS-1$
		PropertyDescriptor licenseProperty = new PropertyDescriptor(Property.LICENSE, "License"); //$NON-NLS-1$
		PropertyDescriptor licenseUrlProperty = new PropertyDescriptor(Property.LICENSE_URL, "License URL"); //$NON-NLS-1$
		PropertyDescriptor repositoryProperty = new PropertyDescriptor(Property.REPOSITORY, "Repository"); //$NON-NLS-1$

		return new IPropertyDescriptor[] { nameProperty, pathProperty, visibleProperty, referenceProperty,
				precedenceProperty, authorProperty, copyrightProperty, descriptionProperty, licenseProperty,
				licenseUrlProperty, repositoryProperty };
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id)
	{
		Object result = null;

		if (id instanceof Property)
		{
			switch ((Property) id)
			{
				case NAME:
					result = this._bundle.getDisplayName();
					break;

				case PATH:
					result = this._bundle.getPath();
					break;

				case VISIBLE:
					result = this._bundle.isVisible();
					break;

				case REFERENCE:
					result = this._bundle.isReference();
					break;
					
				case PRECEDENCE:
					result = this._bundle.getBundlePrecedence();
					break;

				case AUTHOR:
					result = this._bundle.getAuthor();
					break;

				case COPYRIGHT:
					result = this._bundle.getCopyright();
					break;

				case DESCRIPTION:
					result = this._bundle.getDescription();
					break;

				case LICENSE:
					result = this._bundle.getLicense();
					break;

				case LICENSE_URL:
					result = this._bundle.getLicenseUrl();
					break;

				case REPOSITORY:
					result = this._bundle.getRepository();
					break;

				default:
					break;
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#hasChildren()
	 */
	public boolean hasChildren()
	{
		return this._bundle.hasChildren();
	}
}
