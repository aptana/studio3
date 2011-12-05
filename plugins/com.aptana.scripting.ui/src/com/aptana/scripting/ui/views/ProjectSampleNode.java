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

import com.aptana.scripting.model.ProjectSampleElement;
import com.aptana.scripting.ui.ScriptingUIPlugin;

class ProjectSampleNode extends BaseNode<ProjectSampleNode.Property>
{

	enum Property implements IPropertyInformation<ProjectSampleNode>
	{
		NAME(Messages.ProjectSampleNode_Project_Sample_Name)
		{
			public Object getPropertyValue(ProjectSampleNode node)
			{
				return node.projectSample.getDisplayName();
			}
		},
		PATH(Messages.ProjectSampleNode_Project_Sample_Path)
		{
			public Object getPropertyValue(ProjectSampleNode node)
			{
				return node.projectSample.getPath();
			}
		},
		SCOPE(Messages.ProjectSampleNode_Project_Sample_Scope)
		{
			public Object getPropertyValue(ProjectSampleNode node)
			{
				String scope = node.projectSample.getScope();

				return (scope != null && scope.length() > 0) ? scope : Messages.ProjectSampleNode_All_SCOPES;
			}
		},
		CATEGORY(Messages.ProjectSampleNode_Project_Sample_Category)
		{
			public Object getPropertyValue(ProjectSampleNode node)
			{
				return node.projectSample.getCategory();
			}
		},
		LOCATION(Messages.ProjectSampleNode_Project_Sample_Location)
		{
			public Object getPropertyValue(ProjectSampleNode node)
			{
				return node.projectSample.getLocation();
			}
		},
		DESCRIPTION(Messages.ProjectSampleNode_Project_Sample_Description)
		{
			public Object getPropertyValue(ProjectSampleNode node)
			{
				return node.projectSample.getDescription();
			}
		},
		ICON(Messages.ProjectSampleNode_Project_Sample_Icon_Path)
		{
			public Object getPropertyValue(ProjectSampleNode node)
			{
				return node.projectSample.getIcon();
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

	private static final Image PROJECT_SAMPLE_ICON = ScriptingUIPlugin.getImage("icons/sample.png"); //$NON-NLS-1$

	private ProjectSampleElement projectSample;

	/**
	 * Project sample node.
	 * 
	 * @param projectSample
	 */
	ProjectSampleNode(ProjectSampleElement projectSample)
	{
		this.projectSample = projectSample;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getImage()
	 */
	public Image getImage()
	{
		return PROJECT_SAMPLE_ICON;
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
		return projectSample.getDisplayName();
	}
}
