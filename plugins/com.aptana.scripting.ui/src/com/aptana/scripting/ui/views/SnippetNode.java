/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
