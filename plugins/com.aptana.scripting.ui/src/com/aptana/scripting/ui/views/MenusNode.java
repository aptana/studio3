/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.ui.views;

import java.util.Collections;
import java.util.List;

import org.eclipse.swt.graphics.Image;

import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.MenuElement;
import com.aptana.scripting.ui.ScriptingUIPlugin;

class MenusNode extends BaseNode
{
	private static final Image MENUS_ICON = ScriptingUIPlugin.getImage("icons/folder.png"); //$NON-NLS-1$
	private MenuNode[] _menus;

	/**
	 * CommandsNode
	 * 
	 * @param bundle
	 */
	public MenusNode(BundleElement bundle)
	{
		this(bundle.getMenus());
	}

	/**
	 * MenusNode
	 * 
	 * @param elements
	 */
	public MenusNode(List<MenuElement> elements)
	{
		if (elements != null)
		{
			Collections.sort(elements);

			this._menus = new MenuNode[elements.size()];

			for (int i = 0; i < elements.size(); i++)
			{
				this._menus[i] = new MenuNode(elements.get(i));
			}
		}
		else
		{
			this._menus = new MenuNode[0];
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getChildren()
	 */
	public Object[] getChildren()
	{
		return this._menus;
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
		return this._menus.length > 0;
	}
}
