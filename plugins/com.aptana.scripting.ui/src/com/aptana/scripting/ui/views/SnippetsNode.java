/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.ui.views;

import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.graphics.Image;

import com.aptana.core.util.ArrayUtil;
import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.SnippetElement;
import com.aptana.scripting.ui.ScriptingUIPlugin;

class SnippetsNode extends BaseNode<SnippetsNode.Property>
{
	enum Property implements IPropertyInformation<SnippetsNode>
	{
		COUNT(Messages.SnippetsNode_Snippets_Count)
		{
			public Object getPropertyValue(SnippetsNode node)
			{
				return node.snippets.length;
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

	private static final Image SNIPPETS_ICON = ScriptingUIPlugin.getImage("icons/folder.png"); //$NON-NLS-1$
	private SnippetNode[] snippets;

	/**
	 * CommandsNode
	 * 
	 * @param bundle
	 */
	SnippetsNode(BundleElement bundle)
	{
		this(bundle.getCommands());
	}

	/**
	 * SnippetNode
	 * 
	 * @param elements
	 */
	SnippetsNode(List<CommandElement> elements)
	{
		List<SnippetNode> items = new LinkedList<SnippetNode>();

		if (elements != null)
		{
			Collections.sort(elements);

			for (CommandElement command : elements)
			{
				if (command instanceof SnippetElement)
				{
					items.add(new SnippetNode((SnippetElement) command));
				}
			}
		}

		snippets = items.toArray(new SnippetNode[items.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getChildren()
	 */
	public Object[] getChildren()
	{
		return snippets;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getImage()
	 */
	public Image getImage()
	{
		return SNIPPETS_ICON;
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
		return Messages.SnippetsNode_Snippets_Node;
	}

	/**
	 * hasChildren
	 * 
	 * @return
	 */
	public boolean hasChildren()
	{
		return !ArrayUtil.isEmpty(snippets);
	}
}
