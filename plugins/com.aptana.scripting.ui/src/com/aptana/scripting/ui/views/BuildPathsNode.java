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

import org.eclipse.swt.graphics.Image;

import com.aptana.scripting.model.BuildPathElement;
import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.ui.ScriptingUIPlugin;

class BuildPathsNode extends BaseNode<BuildPathsNode.Property>
{
	enum Property implements IPropertyInformation<BuildPathsNode>
	{
		COUNT(Messages.BuildPathsNode_Build_Paths_Count)
		{
			public Object getPropertyValue(BuildPathsNode node)
			{
				return node.buildPaths.length;
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

		public Object getPropertyValue(BuildPathsNode node)
		{
			return null;
		}
	}

	private static final Image BUILD_PATHS_ICON = ScriptingUIPlugin.getImage("icons/folder.png"); //$NON-NLS-1$
	private BuildPathNode[] buildPaths;

	/**
	 * CommandsNode
	 * 
	 * @param bundle
	 */
	BuildPathsNode(BundleElement bundle)
	{
		this(bundle.getBuildPaths());
	}

	/**
	 * FileTemplatesNode
	 * 
	 * @param elements
	 */
	BuildPathsNode(List<BuildPathElement> elements)
	{
		List<BuildPathNode> paths = new ArrayList<BuildPathNode>();

		if (elements != null)
		{
			for (BuildPathElement buildPath : elements)
			{
				paths.add(new BuildPathNode(buildPath));
			}
		}

		buildPaths = paths.toArray(new BuildPathNode[paths.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getChildren()
	 */
	public Object[] getChildren()
	{
		return buildPaths;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getImage()
	 */
	public Image getImage()
	{
		return BUILD_PATHS_ICON;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getLabel()
	 */
	public String getLabel()
	{
		return Messages.BuildPathsNode_Build_Paths;
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

	/**
	 * hasChildren
	 * 
	 * @return
	 */
	public boolean hasChildren()
	{
		return buildPaths.length > 0;
	}
}
