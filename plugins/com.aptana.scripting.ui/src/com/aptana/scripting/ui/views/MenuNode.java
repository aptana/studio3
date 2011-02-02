/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.ui.views;

import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.aptana.scripting.model.MenuElement;
import com.aptana.scripting.ui.ScriptingUIPlugin;

class MenuNode extends BaseNode
{
	private enum Property
	{
		NAME, PATH, SCOPE, SEPARATOR
	}

	private static final Image MENU_ICON = ScriptingUIPlugin.getImage("icons/menu.png"); //$NON-NLS-1$
	private MenuElement _menu;

	/**
	 * MenuNode
	 * 
	 * @param menu
	 */
	public MenuNode(MenuElement menu)
	{
		this._menu = menu;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getChildren()
	 */
	public Object[] getChildren()
	{
		List<MenuElement> children = this._menu.getChildren();
		Object[] result = new Object[children.size()];

		for (int i = 0; i < result.length; i++)
		{
			result[i] = new MenuNode(children.get(i));
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getImage()
	 */
	public Image getImage()
	{
		return MENU_ICON;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getLabel()
	 */
	public String getLabel()
	{
		return this._menu.getDisplayName();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors()
	{
		PropertyDescriptor nameProperty = new PropertyDescriptor(Property.NAME, "Name"); //$NON-NLS-1$
		PropertyDescriptor pathProperty = new PropertyDescriptor(Property.PATH, "Path"); //$NON-NLS-1$
		PropertyDescriptor scopeProperty = new PropertyDescriptor(Property.SCOPE, "Scope"); //$NON-NLS-1$
		PropertyDescriptor separatorProperty = new PropertyDescriptor(Property.SEPARATOR, "Separator"); //$NON-NLS-1$

		return new IPropertyDescriptor[] { nameProperty, pathProperty, scopeProperty, separatorProperty };
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
					result = this._menu.getDisplayName();
					break;

				case PATH:
					result = this._menu.getPath();
					break;

				case SCOPE:
					String scope = this._menu.getScope();

					result = (scope != null && scope.length() > 0) ? scope : "all"; //$NON-NLS-1$
					break;

				case SEPARATOR:
					result = this._menu.isSeparator();
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
		return this._menu.hasChildren();
	}
}
