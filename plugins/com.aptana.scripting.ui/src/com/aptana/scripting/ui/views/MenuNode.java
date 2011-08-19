/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.ui.views;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.graphics.Image;

import com.aptana.scripting.model.MenuElement;
import com.aptana.scripting.ui.ScriptingUIPlugin;

class MenuNode extends BaseNode<MenuNode.Property>
{
	enum Property implements IPropertyInformation<MenuNode>
	{
		NAME(Messages.MenuNode_Menu_Name)
		{
			public Object getPropertyValue(MenuNode node)
			{
				return node.menu.getDisplayName();
			}
		},
		PATH(Messages.MenuNode_Menu_Path)
		{
			public Object getPropertyValue(MenuNode node)
			{
				return node.menu.getPath();
			}
		},
		SCOPE(Messages.MenuNode_Menu_Scope)
		{
			public Object getPropertyValue(MenuNode node)
			{
				String scope = node.menu.getScope();

				return (scope != null && scope.length() > 0) ? scope : "all"; //$NON-NLS-1$
			}
		},
		SEPARATOR(Messages.MenuNode_Menu_Separator)
		{
			public Object getPropertyValue(MenuNode node)
			{
				return node.menu.isSeparator();
			}
		};

		private String header;

		private Property(String header) // $codepro.audit.disable unusedMethod
		{
			this.header = header;
		}

		public String getHeader()
		{
			return header;
		}
	}

	private static final Image MENU_ICON = ScriptingUIPlugin.getImage("icons/menu.png"); //$NON-NLS-1$
	private MenuElement menu;

	/**
	 * MenuNode
	 * 
	 * @param menu
	 */
	MenuNode(MenuElement menu)
	{
		this.menu = menu;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getChildren()
	 */
	public Object[] getChildren()
	{
		List<MenuElement> children = menu.getChildren();
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
	 * @see com.aptana.scripting.ui.views.BaseNode#getPropertyInfoSet()
	 */
	@Override
	protected Set<Property> getPropertyInfoSet()
	{
		return EnumSet.allOf(Property.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getLabel()
	 */
	public String getLabel()
	{
		return menu.getDisplayName();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#hasChildren()
	 */
	public boolean hasChildren()
	{
		return menu.hasChildren();
	}
}
