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
import com.aptana.scripting.model.ProjectSampleElement;
import com.aptana.scripting.ui.ScriptingUIPlugin;

class ProjectSamplesNode extends BaseNode<ProjectSamplesNode.Property>
{
	enum Property implements IPropertyInformation<ProjectSamplesNode>
	{
		COUNT(Messages.ProjectSamplesNode_Project_Samples_Count)
		{
			public Object getPropertyValue(ProjectSamplesNode node)
			{
				return node.projectSamples.length;
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

		public Object getPropertyValue(ProjectSamplesNode node)
		{
			return null;
		}
	}

	private static final Image PROJECT_SAMPLES_ICON = ScriptingUIPlugin.getImage("icons/folder.png"); //$NON-NLS-1$

	private ProjectSampleNode[] projectSamples;

	/**
	 * ProjectSamplesNode
	 * 
	 * @param bundle
	 */
	ProjectSamplesNode(BundleElement bundle)
	{
		this(bundle.getProjectSamples());
	}

	/**
	 * ProjectSamplesNode
	 * 
	 * @param elements
	 */
	ProjectSamplesNode(List<ProjectSampleElement> elements)
	{
		List<ProjectSampleNode> samples = new ArrayList<ProjectSampleNode>();

		if (elements != null)
		{
			for (ProjectSampleElement sample : elements)
			{
				samples.add(new ProjectSampleNode(sample));
			}
		}

		projectSamples = samples.toArray(new ProjectSampleNode[samples.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getChildren()
	 */
	public Object[] getChildren()
	{
		return projectSamples;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.ui.views.BaseNode#getImage()
	 */
	public Image getImage()
	{
		return PROJECT_SAMPLES_ICON;
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
		return Messages.ProjectSamplesNode_NodeName;
	}

	/**
	 * hasChildren
	 * 
	 * @return
	 */
	public boolean hasChildren()
	{
		return projectSamples.length > 0;
	}
}
