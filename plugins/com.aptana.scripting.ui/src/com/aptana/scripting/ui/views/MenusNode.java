package com.aptana.scripting.ui.views;

import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.MenuElement;

class MenusNode implements CollectionNode
{
	private MenuElement[] _menus;
	
	/**
	 * CommandsNode
	 * 
	 * @param bundle
	 */
	public MenusNode(BundleElement bundle)
	{
		this._menus = bundle.getMenus();
	}
	
	/**
	 * getChildren
	 * 
	 * @return
	 */
	public Object[] getChildren()
	{
		return this._menus;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.CollectionNode#getLabel()
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
