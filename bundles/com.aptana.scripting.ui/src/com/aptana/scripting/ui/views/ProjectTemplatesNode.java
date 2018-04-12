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
import com.aptana.scripting.model.ProjectTemplateElement;
import com.aptana.scripting.ui.ScriptingUIPlugin;

class ProjectTemplatesNode extends BaseNode<ProjectTemplatesNode.Property>
{
	enum Property implements IPropertyInformation<ProjectTemplatesNode>
	{
		COUNT(Messages.ProjectTemplatesNode_Project_Templates_Count)
		{
			public Object getPropertyValue(ProjectTemplatesNode node)
			{
				return node.projectTemplates.length;
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

		public Object getPropertyValue(ProjectTemplatesNode node)
		{
			return null;
		}
	}

	private static final Image PROJECT_TEMPLATES_ICON = ScriptingUIPlugin.getImage("icons/folder.png"); //$NON-NLS-1$
	private ProjectTemplateNode[] projectTemplates;

	/**
	 * CommandsNode
	 * 
	 * @param bundle
	 */
	ProjectTemplatesNode(BundleElement bundle)
	{
		this(bundle.getProjectTemplates());
	}

	/**
	 * FileTemplatesNode
	 * 
	 * @param elements
	 */
	ProjectTemplatesNode(List<ProjectTemplateElement> elements)
	{
		List<ProjectTemplateNode> templates = new ArrayList<ProjectTemplateNode>();

		if (elements != null)
		{
			for (ProjectTemplateElement template : elements)
			{
				templates.add(new ProjectTemplateNode(template));
			}
		}

		projectTemplates = templates.toArray(new ProjectTemplateNode[templates.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getChildren()
	 */
	public Object[] getChildren()
	{
		return projectTemplates;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getImage()
	 */
	public Image getImage()
	{
		return PROJECT_TEMPLATES_ICON;
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
		return Messages.ProjectTemplatesNode_Projects_Templates;
	}

	/**
	 * hasChildren
	 * 
	 * @return
	 */
	public boolean hasChildren()
	{
		return projectTemplates.length > 0;
	}
}
