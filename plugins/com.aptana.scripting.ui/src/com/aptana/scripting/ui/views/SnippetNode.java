package com.aptana.scripting.ui.views;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.aptana.scripting.model.SnippetElement;
import com.aptana.scripting.ui.ScriptingUIPlugin;

class SnippetNode extends BaseNode
{
	private static final Image SNIPPET_ICON = ScriptingUIPlugin.getImage("icons/snippet.png"); //$NON-NLS-1$
	
	private static final String BUNDLE_SNIPPET_NAME = "bundle.snippet.name";
	private static final String BUNDLE_SNIPPET_PATH = "bundle.snippet.path";
	private static final String BUNDLE_SNIPPET_TRIGGERS = "bundle.snippet.triggers";
	private static final String BUNDLE_SNIPPET_EXPANSION = "bundle.snippet.expansion";

	private SnippetElement _snippet;

	/**
	 * SnippetNode
	 * 
	 * @param snippet
	 */
	public SnippetNode(SnippetElement snippet)
	{
		this._snippet = snippet;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getImage()
	 */
	public Image getImage()
	{
		return SNIPPET_ICON;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getLabel()
	 */
	public String getLabel()
	{
		return this._snippet.getDisplayName();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors()
	{
		PropertyDescriptor nameProperty = new PropertyDescriptor(BUNDLE_SNIPPET_NAME, "Name");
		PropertyDescriptor pathProperty = new PropertyDescriptor(BUNDLE_SNIPPET_PATH, "Path");
		PropertyDescriptor triggersProperty = new PropertyDescriptor(BUNDLE_SNIPPET_TRIGGERS, "Triggers");
		PropertyDescriptor expansionProperty = new PropertyDescriptor(BUNDLE_SNIPPET_EXPANSION, "Expansion");

		return new IPropertyDescriptor[] { nameProperty, pathProperty, triggersProperty, expansionProperty };
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id)
	{
		Object result = null;

		if (id.equals(BUNDLE_SNIPPET_NAME))
		{
			result = this._snippet.getDisplayName();
		}
		else if (id.equals(BUNDLE_SNIPPET_PATH))
		{
			result = this._snippet.getPath();
		}
		else if (id.equals(BUNDLE_SNIPPET_TRIGGERS))
		{
			String[] triggers = this._snippet.getTriggers();

			if (triggers != null)
			{
				StringBuilder buffer = new StringBuilder();

				for (int i = 0; i < triggers.length; i++)
				{
					if (i > 0)
						buffer.append(", ");

					buffer.append(triggers[i]);
				}

				result = buffer.toString();
			}
		}
		else if (id.equals(BUNDLE_SNIPPET_EXPANSION))
		{
			result = this._snippet.getExpansion();
		}

		return result;
	}
}
