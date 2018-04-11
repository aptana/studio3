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
import com.aptana.scripting.model.ProjectTemplateElement;
import com.aptana.scripting.ui.ScriptingUIPlugin;

class ProjectTemplateNode extends BaseNode<ProjectTemplateNode.Property>
{
	enum Property implements IPropertyInformation<ProjectTemplateNode>
	{
		NAME(Messages.ProjectTemplateNode_Project_Template_Name)
		{
			public Object getPropertyValue(ProjectTemplateNode node)
			{
				return node.projectTemplate.getDisplayName();
			}
		},
		PATH(Messages.ProjectTemplateNode_Project_Template_Path)
		{
			public Object getPropertyValue(ProjectTemplateNode node)
			{
				return node.projectTemplate.getPath();
			}
		},
		SCOPE(Messages.ProjectTemplateNode_Project_Template_Scope)
		{
			public Object getPropertyValue(ProjectTemplateNode node)
			{
				String scope = node.projectTemplate.getScope();

				return (scope != null && scope.length() > 0) ? scope : Messages.ProjectTemplateNode_All_Scopes;
			}
		},
		TYPE(Messages.ProjectTemplateNode_Project_Template_File_Type)
		{
			public Object getPropertyValue(ProjectTemplateNode node)
			{
				return node.projectTemplate.getTypeString();
			}
		},
		LOCATION(Messages.ProjectTemplateNode_Project_Template_Location)
		{
			public Object getPropertyValue(ProjectTemplateNode node)
			{
				return node.projectTemplate.getLocation();
			}
		},
		DESCRIPTION(Messages.ProjectTemplateNode_Project_Template_Description)
		{
			public Object getPropertyValue(ProjectTemplateNode node)
			{
				return node.projectTemplate.getDescription();
			}
		},
		ICON(Messages.ProjectTemplateNode_Project_Template_Icon_Path)
		{
			public Object getPropertyValue(ProjectTemplateNode node)
			{
				return node.projectTemplate.getIcon();
			}
		},
		TAGS(Messages.ProjectTemplateNode_Project_Template_Tags)
		{
			public Object getPropertyValue(ProjectTemplateNode node)
			{
				return StringUtil.join(",", node.projectTemplate.getTags()); //$NON-NLS-1$
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

	private static final Image PROJECT_TEMPLATE_ICON = ScriptingUIPlugin.getImage("icons/template.png"); //$NON-NLS-1$
	private ProjectTemplateElement projectTemplate;

	/**
	 * SnippetNode
	 * 
	 * @param projectTemplate
	 */
	ProjectTemplateNode(ProjectTemplateElement projectTemplate)
	{
		this.projectTemplate = projectTemplate;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getImage()
	 */
	public Image getImage()
	{
		return PROJECT_TEMPLATE_ICON;
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
		return projectTemplate.getDisplayName();
	}
}
