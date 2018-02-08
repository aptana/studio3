/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.ui.views;

import java.util.EnumSet;
import java.util.Set;

import org.eclipse.swt.graphics.Image;

import com.aptana.core.util.StringUtil;
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
				return StringUtil.join(", ", node.snippet.getTriggerTypeValues(TriggerType.PREFIX)); //$NON-NLS-1$
			}
		},
		EXPANSION(Messages.SnippetNode_Snippet_Expansion)
		{
			public Object getPropertyValue(SnippetNode node)
			{
				return node.snippet.getDisplayName();
			}
		},
		CATEGORY(Messages.SnippetNode_Category)
		{
			public Object getPropertyValue(SnippetNode node)
			{
				return node.snippet.getCategory();
			}
		},
		ICON_PATH(Messages.SnippetNode_Icon_Path)
		{
			public Object getPropertyValue(SnippetNode node)
			{
				return node.snippet.getIconPath();
			}
		},
		TAGS(Messages.SnippetNode_Tags)
		{
			public Object getPropertyValue(SnippetNode node)
			{
				return StringUtil.join(", ", node.snippet.getTags()); //$NON-NLS-1$
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
