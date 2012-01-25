/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.ui.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.graphics.Image;

import com.aptana.core.util.ArrayUtil;
import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.SnippetCategoryElement;
import com.aptana.scripting.ui.ScriptingUIPlugin;

/**
 * A node in the Bundles view that contains the defined SnippetCategoryNodes
 * 
 * @author nle
 */
public class SnippetCategoriesNode extends BaseNode<SnippetCategoriesNode.Property>
{
	enum Property implements IPropertyInformation<SnippetCategoriesNode>
	{
		COUNT(Messages.SnippetsNode_Snippets_Count)
		{
			public Object getPropertyValue(SnippetCategoriesNode node)
			{
				return node.snippetCategories.length;
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

	private static final Image SNIPPET_CATEGORIES_ICON = ScriptingUIPlugin.getImage("icons/folder.png"); //$NON-NLS-1$
	private SnippetCategoryNode[] snippetCategories;

	/**
	 * CommandsNode
	 * 
	 * @param bundle
	 */
	SnippetCategoriesNode(BundleElement bundle)
	{
		this(bundle.getSnippetCategories());
	}

	/**
	 * SnippetNode
	 * 
	 * @param elements
	 */
	SnippetCategoriesNode(List<SnippetCategoryElement> elements)
	{
		List<SnippetCategoryNode> items = new ArrayList<SnippetCategoryNode>();

		if (elements != null)
		{
			Collections.sort(elements);

			for (SnippetCategoryElement element : elements)
			{
				if (element instanceof SnippetCategoryElement)
				{
					items.add(new SnippetCategoryNode((SnippetCategoryElement) element));
				}
			}
		}

		snippetCategories = items.toArray(new SnippetCategoryNode[items.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getChildren()
	 */
	public Object[] getChildren()
	{
		return snippetCategories;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getImage()
	 */
	public Image getImage()
	{
		return SNIPPET_CATEGORIES_ICON;
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
		return Messages.SnippetCategoriesNode_SnippetCategories_Node;
	}

	/**
	 * hasChildren
	 * 
	 * @return
	 */
	public boolean hasChildren()
	{
		return !ArrayUtil.isEmpty(snippetCategories);
	}
}
