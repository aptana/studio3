package com.aptana.scripting.ui.views;

import java.util.Arrays;

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
	public MenusNode(MenuElement[] elements)
	{
		if (elements != null)
		{
			Arrays.sort(elements);

			this._menus = new MenuNode[elements.length];

			for (int i = 0; i < elements.length; i++)
			{
				this._menus[i] = new MenuNode(elements[i]);
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
