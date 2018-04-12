/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.ui.views;

import java.io.File;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.graphics.Image;

import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.ui.ScriptingUIPlugin;

class BundleNode extends BaseNode<BundleNode.Property>
{
	enum Property implements IPropertyInformation<BundleNode>
	{
		NAME(Messages.BundleNode_Bundle_Name)
		{
			public Object getPropertyValue(BundleNode node)
			{
				return node.bundle.getDisplayName();
			}
		},
		PATH(Messages.BundleNode_Bundle_Path)
		{
			public Object getPropertyValue(BundleNode node)
			{
				return node.bundle.getPath();
			}
		},
		VISIBLE(Messages.BundleNode_Bundle_Visible)
		{
			public Object getPropertyValue(BundleNode node)
			{
				return node.bundle.isVisible();
			}
		},
		REFERENCE(Messages.BundleNode_Bundle_Reference)
		{
			public Object getPropertyValue(BundleNode node)
			{
				return node.bundle.isReference();
			}
		},
		PRECEDENCE(Messages.BundleNode_Bundle_Precedence)
		{
			public Object getPropertyValue(BundleNode node)
			{
				return node.bundle.getBundlePrecedence();
			}
		},
		AUTHOR(Messages.BundleNode_Bundle_Author)
		{
			public Object getPropertyValue(BundleNode node)
			{
				return node.bundle.getAuthor();
			}
		},
		COPYRIGHT(Messages.BundleNode_Bundle_Copyright)
		{
			public Object getPropertyValue(BundleNode node)
			{
				return node.bundle.getCopyright();
			}
		},
		DESCRIPTION(Messages.BundleNode_Bundle_Description)
		{
			public Object getPropertyValue(BundleNode node)
			{
				return node.bundle.getDescription();
			}
		},
		LICENSE(Messages.BundleNode_Bundle_License)
		{
			public Object getPropertyValue(BundleNode node)
			{
				return node.bundle.getLicense();
			}
		},
		LICENSE_URL(Messages.BundleNode_Bundle_License_URL)
		{
			public Object getPropertyValue(BundleNode node)
			{
				return node.bundle.getLicenseUrl();
			}
		},
		REPOSITORY(Messages.BundleNode_Bundle_Repository)
		{
			public Object getPropertyValue(BundleNode node)
			{
				return node.bundle.getRepository();
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

	private static final Image BUNDLE_ICON = ScriptingUIPlugin.getImage("icons/bundle_directory.png"); //$NON-NLS-1$
	private BundleElement bundle;

	/**
	 * BundleNode
	 * 
	 * @param bundle
	 */
	BundleNode(BundleElement bundle)
	{
		this.bundle = bundle;
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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getChildren()
	 */
	public Object[] getChildren()
	{
		List<Object> items = new LinkedList<Object>();

		// create and add children
		addNode(items, new CommandsNode(bundle));
		addNode(items, new SnippetsNode(bundle));
		addNode(items, new SnippetCategoriesNode(bundle));
		addNode(items, new FileTemplatesNode(bundle));
		addNode(items, new MenusNode(bundle));
		addNode(items, new BuildPathsNode(bundle));
		addNode(items, new EnvsNode(bundle));
		addNode(items, new ProjectTemplatesNode(bundle));
		addNode(items, new ProjectSamplesNode(bundle));

		return items.toArray(new Object[items.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getImage()
	 */
	public Image getImage()
	{
		return BUNDLE_ICON;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getLabel()
	 */
	public String getLabel()
	{
		File file = new File(bundle.getPath());

		return file.getAbsolutePath();
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
		return bundle.hasChildren();
	}
}
