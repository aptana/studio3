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

import com.aptana.scripting.model.BuildPathElement;
import com.aptana.scripting.ui.ScriptingUIPlugin;

class BuildPathNode extends BaseNode<BuildPathNode.Property>
{
	enum Property implements IPropertyInformation<BuildPathNode>
	{
		NAME(Messages.BuildPathNode_Build_Path_Name)
		{
			public Object getPropertyValue(BuildPathNode node)
			{
				return node.buildPath.getDisplayName();
			}
		},
		PATH(Messages.BuildPathNode_Build_Path_Path)
		{
			public Object getPropertyValue(BuildPathNode node)
			{
				return node.buildPath.getPath();
			}
		},
		SCOPE(Messages.BuildPathNode_Build_Path_Scope)
		{
			public Object getPropertyValue(BuildPathNode node)
			{
				String scope = node.buildPath.getScope();

				return (scope != null && scope.length() > 0) ? scope : Messages.BuildPathNode_All_Scopes;
			}
		},
		BUILD_PATH(Messages.BuildPathNode_Build_Path_Build_Path)
		{
			public Object getPropertyValue(BuildPathNode node)
			{
				return node.buildPath.getBuildPath();
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

	private static final Image BUILD_PATH_ICON = ScriptingUIPlugin.getImage("icons/template.png"); //$NON-NLS-1$
	private BuildPathElement buildPath;

	/**
	 * BuildPathNode
	 * 
	 * @param buildPath
	 */
	BuildPathNode(BuildPathElement buildPath)
	{
		this.buildPath = buildPath;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getImage()
	 */
	public Image getImage()
	{
		return BUILD_PATH_ICON;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getLabel()
	 */
	public String getLabel()
	{
		return buildPath.getDisplayName();
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
}
