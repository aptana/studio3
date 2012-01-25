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

import com.aptana.scripting.model.SnippetCategoryElement;
import com.aptana.scripting.ui.ScriptingUIPlugin;

/**
 * Node in the Bundle view that represents SnippetCategoryElements
 * 
 * @author nle
 */
public class SnippetCategoryNode extends BaseNode<SnippetCategoryNode.Property>
{
	enum Property implements IPropertyInformation<SnippetCategoryNode>
	{
		NAME(Messages.SnippetCategoryNode_Snippet_Name)
		{
			public Object getPropertyValue(SnippetCategoryNode node)
			{
				return node.snippetCategory.getDisplayName();
			}
		},
		PATH(Messages.SnippetCategoryNode_Snippet_Path)
		{
			public Object getPropertyValue(SnippetCategoryNode node)
			{
				return node.snippetCategory.getPath();
			}
		},
		SCOPE(Messages.SnippetCategoryNode_Snippet_Scope)
		{
			public Object getPropertyValue(SnippetCategoryNode node)
			{
				String scope = node.snippetCategory.getScope();

				return (scope != null && scope.length() > 0) ? scope : "all"; //$NON-NLS-1$
			}
		},
		ICON_PATH(Messages.SnippetCategoryNode_Snippet_Icon_Path)
		{
			public Object getPropertyValue(SnippetCategoryNode node)
			{
				return node.snippetCategory.getIconPath();
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

	private static final Image SNIPPET_CATEGORY_ICON = ScriptingUIPlugin.getImage("icons/snippet_category.png"); //$NON-NLS-1$
	private SnippetCategoryElement snippetCategory;

	/**
	 * SnippetNode
	 * 
	 * @param snippet
	 */
	public SnippetCategoryNode(SnippetCategoryElement category)
	{
		this.snippetCategory = category;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getImage()
	 */
	public Image getImage()
	{
		return SNIPPET_CATEGORY_ICON;
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
		return snippetCategory.getDisplayName();
	}
}
