/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.ui.views;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;

import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.BundleEntry;
import com.aptana.scripting.ui.ScriptingUIPlugin;

class BundleEntryNode extends BaseNode<BundleEntryNode.Property>
{
	enum Property implements IPropertyInformation<BundleEntryNode>
	{
		NAME(Messages.BundleEntryNode_Bundle_Entry_Name)
		{
			public Object getPropertyValue(BundleEntryNode node)
			{
				return (node.entry == null) ? null : node.entry.getName();
			}
		},
		CONTRIBUTOR_COUNT(Messages.BundleEntryNode_Bundle_Entry_Contributor_Count)
		{
			public Object getPropertyValue(BundleEntryNode node)
			{
				return (node.entry == null) ? null : node.entry.getBundles().size();
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

	private static final Image BUNDLE_ENTRY_ICON = ScriptingUIPlugin.getImage("icons/bundle_entry.png"); //$NON-NLS-1$

	private BundleEntry entry;
	private Action reloadAction;

	/**
	 * BundleEntryNode
	 * 
	 * @param entry
	 */
	BundleEntryNode(BundleEntry entry)
	{
		this.entry = entry;

		makeActions();
	}

	/**
	 * addNode
	 * 
	 * @param items
	 * @param node
	 */
	private void addNode(List<Object> items, BaseNode<?> node)
	{
		if (node != null && node.hasChildren())
		{
			items.add(node);
		}
	}

	/**
	 * getActions
	 */
	public Action[] getActions()
	{
		return new Action[] { reloadAction };
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getChildren()
	 */
	public Object[] getChildren()
	{
		if (entry == null)
		{
			return ArrayUtil.NO_STRINGS;
		}
		List<Object> result = new ArrayList<Object>();

		// add bundle elements that contribute to this bundle
		for (BundleElement bundle : entry.getBundles())
		{
			result.add(new BundleNode(bundle));
		}

		// create and add children
		addNode(result, new CommandsNode(entry.getCommands()));
		addNode(result, new SnippetsNode(entry.getCommands()));
		addNode(result, new SnippetCategoriesNode(entry.getSnippetCategories()));
		addNode(result, new FileTemplatesNode(entry.getCommands()));
		addNode(result, new MenusNode(entry.getMenus()));
		addNode(result, new BuildPathsNode(entry.getBuildPaths()));
		addNode(result, new EnvsNode(entry.getEnvs()));
		addNode(result, new ProjectTemplatesNode(entry.getProjectTemplates()));
		addNode(result, new ProjectSamplesNode(entry.getProjectSamples()));

		return result.toArray();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getImage()
	 */
	public Image getImage()
	{
		return BUNDLE_ENTRY_ICON;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getLabel()
	 */
	public String getLabel()
	{
		return (entry == null) ? StringUtil.EMPTY : entry.getName();
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
	 * @see com.aptana.scripting.ui.views.BaseNode#hasChildren()
	 */
	public boolean hasChildren()
	{
		if (entry == null)
		{
			return false;
		}

		for (BundleElement bundle : entry.getBundles())
		{
			if (bundle.hasChildren())
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * makeActions
	 */
	private void makeActions()
	{
		reloadAction = new Action()
		{
			public void run()
			{
				entry.reload();
			}
		};
		reloadAction.setText(Messages.BundleEntryNode_TXT_Reload);
		// reloadAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_));
	}
}
