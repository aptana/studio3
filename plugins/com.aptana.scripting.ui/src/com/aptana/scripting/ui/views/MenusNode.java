/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.ui.views;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.graphics.Image;

import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.MenuElement;
import com.aptana.scripting.ui.ScriptingUIPlugin;

class MenusNode extends BaseNode<MenusNode.Property>
{
	enum Property implements IPropertyInformation<MenusNode>
	{
		COUNT(Messages.MenusNode_Menus_Count)
		{
			public Object getPropertyValue(MenusNode node)
			{
				return node.menus.length;
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

		public Object getPropertyValue(MenusNode node)
		{
			return null;
		}
	}

	private static final Image MENUS_ICON = ScriptingUIPlugin.getImage("icons/folder.png"); //$NON-NLS-1$
	private MenuNode[] menus;

	/**
	 * CommandsNode
	 * 
	 * @param bundle
	 */
	MenusNode(BundleElement bundle)
	{
		this(bundle.getMenus());
	}

	/**
	 * MenusNode
	 * 
	 * @param elements
	 */
	MenusNode(List<MenuElement> elements)
	{
		if (elements != null)
		{
			Collections.sort(elements);

			menus = new MenuNode[elements.size()];

			for (int i = 0; i < elements.size(); i++)
			{
				menus[i] = new MenuNode(elements.get(i));
			}
		}
		else
		{
			menus = new MenuNode[0]; // $codepro.audit.disable reusableImmutables
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getChildren()
	 */
	public Object[] getChildren()
	{
		return menus;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getImage()
	 */
	public Image getImage()
	{
		return MENUS_ICON;
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
		return Messages.MenusNode_Menus_Node;
	}

	/**
	 * hasChildren
	 * 
	 * @return
	 */
	public boolean hasChildren()
	{
		return menus.length > 0;
	}
}
