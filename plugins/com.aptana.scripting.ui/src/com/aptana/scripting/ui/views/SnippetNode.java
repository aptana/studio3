package com.aptana.scripting.ui.views;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.aptana.scripting.model.SnippetElement;
import com.aptana.scripting.ui.ScriptingUIPlugin;

class SnippetNode extends BaseNode
{
	private enum Property
	{
		NAME, PATH, SCOPE, TRIGGERS, EXPANSION
	}

	private static final Image SNIPPET_ICON = ScriptingUIPlugin.getImage("icons/snippet.png"); //$NON-NLS-1$
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
		PropertyDescriptor nameProperty = new PropertyDescriptor(Property.NAME, "Name");
		PropertyDescriptor pathProperty = new PropertyDescriptor(Property.PATH, "Path");
		PropertyDescriptor scopeProperty = new PropertyDescriptor(Property.SCOPE, "Scope");
		PropertyDescriptor triggersProperty = new PropertyDescriptor(Property.TRIGGERS, "Triggers");
		PropertyDescriptor expansionProperty = new PropertyDescriptor(Property.EXPANSION, "Expansion");

		return new IPropertyDescriptor[] { nameProperty, pathProperty, scopeProperty, triggersProperty,
				expansionProperty };
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
					result = this._snippet.getDisplayName();
					break;

				case PATH:
					result = this._snippet.getPath();
					break;

				case SCOPE:
					String scope = this._snippet.getScope();

					result = (scope != null && scope.length() > 0) ? scope : "all";
					break;

				case TRIGGERS:
					String[] triggers = this._snippet.getTriggers();

					if (triggers != null)
					{
						StringBuilder buffer = new StringBuilder();

						for (int i = 0; i < triggers.length; i++)
						{
							if (i > 0)
							{
								buffer.append(", ");
							}

							buffer.append(triggers[i]);
						}

						result = buffer.toString();
					}
					break;

				case EXPANSION:
					result = this._snippet.getExpansion();
					break;
			}
		}

		return result;
	}
}
