package com.aptana.scripting.ui.views;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.BundleEntry;
import com.aptana.scripting.ui.ScriptingUIPlugin;

public class BundleEntryNode extends BaseNode
{
	private static final Image BUNDLE_ENTRY_ICON = ScriptingUIPlugin.getImage("icons/bundle_entry.png"); //$NON-NLS-1$
	
	private static final String BUNDLE_ENTRY_NAME = "bundle.entry.name";

	private BundleEntry _entry;

	/**
	 * BundleEntryNode
	 * 
	 * @param entry
	 */
	public BundleEntryNode(BundleEntry entry)
	{
		this._entry = entry;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.CollectionNode#getChildren()
	 */
	public Object[] getChildren()
	{
		BundleElement[] bundles = this._entry.getBundles();
		Object[] result = new Object[bundles.length];

		for (int i = 0; i < bundles.length; i++)
		{
			result[i] = new BundleNode(bundles[i]);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.CollectionNode#getImage()
	 */
	public Image getImage()
	{
		return BUNDLE_ENTRY_ICON;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.CollectionNode#getLabel()
	 */
	public String getLabel()
	{
		return this._entry.getName();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors()
	{
		PropertyDescriptor nameProperty = new PropertyDescriptor(BUNDLE_ENTRY_NAME, "Name");

		return new IPropertyDescriptor[] { nameProperty };
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id)
	{
		Object result = null;

		if (id.equals(BUNDLE_ENTRY_NAME))
		{
			result = this._entry.getName();
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#hasChildren()
	 */
	public boolean hasChildren()
	{
		return this._entry.size() > 0;
	}
}
