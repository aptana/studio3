/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.ui.views;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.aptana.scripting.model.SnippetElement;
import com.aptana.scripting.model.TriggerType;
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
		PropertyDescriptor nameProperty = new PropertyDescriptor(Property.NAME, "Name"); //$NON-NLS-1$
		PropertyDescriptor pathProperty = new PropertyDescriptor(Property.PATH, "Path"); //$NON-NLS-1$
		PropertyDescriptor scopeProperty = new PropertyDescriptor(Property.SCOPE, "Scope"); //$NON-NLS-1$
		PropertyDescriptor triggersProperty = new PropertyDescriptor(Property.TRIGGERS, "Triggers"); //$NON-NLS-1$
		PropertyDescriptor expansionProperty = new PropertyDescriptor(Property.EXPANSION, "Expansion"); //$NON-NLS-1$

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

					result = (scope != null && scope.length() > 0) ? scope : "all"; //$NON-NLS-1$
					break;

				case TRIGGERS:
					String[] triggers = this._snippet.getTriggerTypeValues(TriggerType.PREFIX);

					if (triggers != null)
					{
						StringBuilder buffer = new StringBuilder();

						for (int i = 0; i < triggers.length; i++)
						{
							if (i > 0)
							{
								buffer.append(", "); //$NON-NLS-1$
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
