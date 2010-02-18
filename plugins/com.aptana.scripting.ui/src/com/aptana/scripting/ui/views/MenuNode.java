package com.aptana.scripting.ui.views;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.aptana.scripting.model.MenuElement;
import com.aptana.scripting.ui.ScriptingUIPlugin;

public class MenuNode extends BaseNode
{
	private static final Image MENU_ICON = ScriptingUIPlugin.getImage("icons/menu.png"); //$NON-NLS-1$
	
	private static final String BUNDLE_MENU_NAME = "bundle.menu.name";
	private static final String BUNDLE_MENU_PATH = "bundle.menu.path";
	
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
		MenuElement[] children = this._menu.getChildren();
		Object[] result = new Object[children.length];
		
		for (int i = 0; i < result.length; i++)
		{
			result[i] = new MenuNode(children[i]);
		}
		
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.CollectionNode#getImage()
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
		PropertyDescriptor nameProperty = new PropertyDescriptor(BUNDLE_MENU_NAME, "Name");
		PropertyDescriptor pathProperty = new PropertyDescriptor(BUNDLE_MENU_PATH, "Path");
		
		return new IPropertyDescriptor[] { nameProperty, pathProperty };
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id)
	{
		Object result = null;

		if (id.equals(BUNDLE_MENU_NAME))
		{
			result = this._menu.getDisplayName();
		}
		else if (id.equals(BUNDLE_MENU_PATH))
		{
			result = this._menu.getPath();
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
