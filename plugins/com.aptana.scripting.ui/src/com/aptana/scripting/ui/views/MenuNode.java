package com.aptana.scripting.ui.views;

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
		PropertyDescriptor nameProperty = new PropertyDescriptor(Property.NAME, "Name");
		PropertyDescriptor pathProperty = new PropertyDescriptor(Property.PATH, "Path");
		PropertyDescriptor scopeProperty = new PropertyDescriptor(Property.SCOPE, "Scope");
		PropertyDescriptor separatorProperty = new PropertyDescriptor(Property.SEPARATOR, "Separator");

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

					result = (scope != null && scope.length() > 0) ? scope : "all";
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
