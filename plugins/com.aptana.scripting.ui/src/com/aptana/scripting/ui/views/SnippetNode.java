/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.ui.views;

import java.util.EnumSet;
import java.util.Set;

import org.eclipse.swt.graphics.Image;

import com.aptana.scripting.model.SnippetElement;
import com.aptana.scripting.model.TriggerType;
import com.aptana.scripting.ui.ScriptingUIPlugin;

class SnippetNode extends BaseNode<SnippetNode.Property>
{
	enum Property implements IPropertyInformation<SnippetNode>
	{
		NAME(Messages.SnippetNode_Snippet_Name)
		{
			public Object getPropertyValue(SnippetNode node)
			{
				return node.snippet.getDisplayName();
			}
		},
		PATH(Messages.SnippetNode_Snippet_Path)
		{
			public Object getPropertyValue(SnippetNode node)
			{
				return node.snippet.getPath();
			}
		},
		SCOPE(Messages.SnippetNode_Snippet_Scope)
		{
			public Object getPropertyValue(SnippetNode node)
			{
				String scope = node.snippet.getScope();

				return (scope != null && scope.length() > 0) ? scope : "all"; //$NON-NLS-1$
			}
		},
		TRIGGERS(Messages.SnippetNode_Snippet_Triggers)
		{
			public Object getPropertyValue(SnippetNode node)
			{
				String[] triggers = node.snippet.getTriggerTypeValues(TriggerType.PREFIX);

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

					return buffer.toString();
				}

				return null;
			}
		},
		EXPANSION(Messages.SnippetNode_Snippet_Expansion)
		{
			public Object getPropertyValue(SnippetNode node)
			{
				return node.snippet.getDisplayName();
			}
		};

		private String header;

		private Property(String header) // $codepro.audit.disable unusedMethod
		{
			this.header = header;
		}

		public String getHeader()
		{
			return header;
		}
	}

	private static final Image SNIPPET_ICON = ScriptingUIPlugin.getImage("icons/snippet.png"); //$NON-NLS-1$
	private SnippetElement snippet;

	/**
	 * SnippetNode
	 * 
	 * @param snippet
	 */
	SnippetNode(SnippetElement snippet)
	{
		this.snippet = snippet;
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
	 * @see com.aptana.scripting.ui.views.BaseNode#getPropertyInfoSet()
	 */
	@Override
	protected Set<Property> getPropertyInfoSet()
	{
		return EnumSet.allOf(Property.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getLabel()
	 */
	public String getLabel()
	{
		return snippet.getDisplayName();
	}
}
