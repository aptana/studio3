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

import com.aptana.scripting.model.EnvironmentElement;
import com.aptana.scripting.ui.ScriptingUIPlugin;

class EnvNode extends BaseNode<EnvNode.Property>
{
	enum Property implements IPropertyInformation<EnvNode>
	{
		NAME(Messages.EnvNode_Env_Name)
		{
			public Object getPropertyValue(EnvNode node)
			{
				return node.env.getDisplayName();
			}
		},
		PATH(Messages.EnvNode_Env_Path)
		{
			public Object getPropertyValue(EnvNode node)
			{
				return node.env.getPath();
			}
		},
		SCOPE(Messages.EnvNode_Env_Scope)
		{
			public Object getPropertyValue(EnvNode node)
			{
				String scope = node.env.getScope();

				return (scope != null && scope.length() > 0) ? scope : Messages.EnvNode_All_Scopes;
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

	private static final Image ENV_ICON = ScriptingUIPlugin.getImage("icons/template.png"); //$NON-NLS-1$
	private EnvironmentElement env;

	/**
	 * SnippetNode
	 * 
	 * @param env
	 */
	EnvNode(EnvironmentElement env)
	{
		this.env = env;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getImage()
	 */
	public Image getImage()
	{
		return ENV_ICON;
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
		return env.getDisplayName();
	}
}
