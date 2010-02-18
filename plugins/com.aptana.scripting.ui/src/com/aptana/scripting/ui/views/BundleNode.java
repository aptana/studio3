package com.aptana.scripting.ui.views;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.ui.ScriptingUIPlugin;

public class BundleNode extends BaseNode
{
	private static final Image BUNDLE_ICON = ScriptingUIPlugin.getImage("icons/folder.png"); //$NON-NLS-1$
	
	private static final String BUNDLE_NAME = "bundle.name";
	private static final String BUNDLE_PATH = "bundle.path";
	
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
	 * @see com.aptana.scripting.ui.views.CollectionNode#getChildren()
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
	 * @see com.aptana.scripting.ui.views.CollectionNode#getImage()
	 */
	public Image getImage()
	{
		return BUNDLE_ICON;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.CollectionNode#getLabel()
	 */
	public String getLabel()
	{
		File file = new File(this._bundle.getPath());

		return file.getAbsolutePath();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors()
	{
		PropertyDescriptor nameProperty = new PropertyDescriptor(BUNDLE_NAME, "Name");
		PropertyDescriptor pathProperty = new PropertyDescriptor(BUNDLE_PATH, "Path");
		
		return new IPropertyDescriptor[] { nameProperty, pathProperty };
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id)
	{
		Object result = null;
		
		if (id.equals(BUNDLE_NAME))
		{
			result = this._bundle.getDisplayName();
		}
		else if (id.equals(BUNDLE_PATH))
		{
			result = this._bundle.getPath();
		}
		
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.CollectionNode#hasChildren()
	 */
	public boolean hasChildren()
	{
		return this._bundle.hasCommands() || this._bundle.hasMenus();
	}
}
