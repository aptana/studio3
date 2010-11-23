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
