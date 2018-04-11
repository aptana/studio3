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

import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.EnvironmentElement;
import com.aptana.scripting.ui.ScriptingUIPlugin;

class EnvsNode extends BaseNode<EnvsNode.Property>
{
	enum Property implements IPropertyInformation<EnvsNode>
	{
		COUNT(Messages.EnvsNode_Envs_Count)
		{
			public Object getPropertyValue(EnvsNode node)
			{
				return node.envs.length;
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

		public Object getPropertyValue(EnvsNode node)
		{
			return null;
		}
	}

	private static final Image ENVS_ICON = ScriptingUIPlugin.getImage("icons/folder.png"); //$NON-NLS-1$
	private EnvNode[] envs;

	/**
	 * CommandsNode
	 * 
	 * @param bundle
	 */
	EnvsNode(BundleElement bundle)
	{
		this(bundle.getEnvs());
	}

	/**
	 * FileTemplatesNode
	 * 
	 * @param elements
	 */
	EnvsNode(List<EnvironmentElement> elements)
	{
		List<EnvNode> items = new ArrayList<EnvNode>();

		if (elements != null)
		{
			for (EnvironmentElement env : elements)
			{
				items.add(new EnvNode(env));
			}
		}

		envs = items.toArray(new EnvNode[items.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getChildren()
	 */
	public Object[] getChildren()
	{
		return envs;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getImage()
	 */
	public Image getImage()
	{
		return ENVS_ICON;
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
		return Messages.EnvsNode_Envs;
	}

	/**
	 * hasChildren
	 * 
	 * @return
	 */
	public boolean hasChildren()
	{
		return envs.length > 0;
	}
}
