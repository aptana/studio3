/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;

import com.aptana.core.projects.templates.IProjectTemplate;
import com.aptana.core.projects.templates.ProjectTemplate;
import com.aptana.core.projects.templates.TemplateType;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.SourcePrinter;
import com.aptana.core.util.StringUtil;

public class ProjectTemplateElement extends AbstractBundleElement implements IProjectTemplate
{
	private TemplateType fType = TemplateType.UNDEFINED;
	private String fLocation;
	private String fDescription;
	private String fId;
	private String fIconPath;
	private URL fIconURL;
	private int fPriority;
	private List<String> fTags;

	/**
	 * ProjectTemplate
	 * 
	 * @param path
	 */
	public ProjectTemplateElement(String path)
	{
		super(path);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof ProjectTemplateElement))
		{
			return false;
		}
		ProjectTemplateElement other = (ProjectTemplateElement) obj;
		return getType() == other.getType() && getDisplayName().equals(other.getDisplayName())
				&& getLocation().equals(other.getLocation());
	}

	/**
	 * getDescription
	 * 
	 * @return
	 */
	public String getDescription()
	{
		return fDescription;
	}

	/**
	 * getDirectory
	 * 
	 * @return
	 */
	public File getDirectory()
	{
		return this.getOwningBundle().getBundleDirectory();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.CommandElement#getElementName()
	 */
	public String getElementName()
	{
		return "project_template"; //$NON-NLS-1$
	}

	/**
	 * getLocation
	 * 
	 * @return
	 */
	public String getLocation()
	{
		return fLocation;
	}

	/**
	 * getType
	 * 
	 * @return
	 */
	public TemplateType getType()
	{
		return fType;
	}

	/**
	 * getTypeString
	 * 
	 * @return
	 */
	public String getTypeString()
	{
		return fType.toString();
	}

	/**
	 * @return the tags
	 */
	public List<String> getTags()
	{
		return CollectionsUtil.getListValue(fTags);
	}

	@Override
	public int hashCode()
	{
		int hash = 31 + getType().hashCode();
		hash = 31 * hash + getDisplayName().hashCode();
		hash = 31 * hash + getLocation().hashCode();
		return hash;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.CommandElement#printBody(com.aptana.core.util.SourcePrinter)
	 */
	protected void printBody(SourcePrinter printer, boolean includeBlocks)
	{
		ProjectTemplate.printBody(printer, includeBlocks, this);
	}

	/**
	 * setDescription
	 * 
	 * @param description
	 */
	public void setDescription(String description)
	{
		fDescription = description;
	}

	/**
	 * setLocation
	 * 
	 * @param location
	 */
	public void setLocation(String location)
	{
		fLocation = location;
	}

	/**
	 * setType
	 * 
	 * @param type
	 */
	public void setType(String type)
	{
		try
		{
			fType = TemplateType.valueOf(type.toUpperCase());
		}
		catch (Exception e)
		{
			fType = TemplateType.UNDEFINED;
		}
	}

	/**
	 * setType
	 * 
	 * @param type
	 */
	public void setType(TemplateType type)
	{
		fType = type;
	}

	/**
	 * @param tags
	 *            the tags to set
	 */
	public void setTags(List<String> tags)
	{
		fTags = CollectionsUtil.isEmpty(tags) ? null : new ArrayList<String>(tags);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.projects.templates.IProjectTemplate#getIconPath()
	 */
	public URL getIconURL()
	{
		if (fIconURL != null)
		{
			return fIconURL;
		}

		fIconURL = getURLFromPath(fIconPath);
		return fIconURL;

	}

	/*
	 * setIconPath
	 */
	public void setIcon(String iconPath)
	{
		fIconPath = iconPath;
		fIconURL = null;
	}

	/*
	 * getIconPath
	 */
	public String getIcon()
	{
		return fIconPath;
	}

	public int getPriority()
	{
		return fPriority;
	}

	public void setPriority(int priority)
	{
		fPriority = priority;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.projects.templates.IProjectTemplate#isReplacingParameters()
	 */
	public boolean isReplacingParameters()
	{
		Object replace = get("replace_parameters"); //$NON-NLS-1$
		if (replace == null)
		{
			return false;
		}
		return Boolean.parseBoolean(replace.toString());
	}

	/**
	 * setId
	 * 
	 * @param id
	 */
	public void setId(String id)
	{
		this.fId = id;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.projects.templates.IProjectTemplate#getId()
	 */
	public String getId()
	{
		return fId;
	}

	@Override
	public void setDisplayName(String displayName)
	{
		super.setDisplayName(displayName);

		if (StringUtil.EMPTY.equals(getId()) || getId() == null)
		{
			setId(displayName);
		}
	}

	public IStatus apply(IProject project, boolean promptForOverwrite)
	{
		return createProjectTemplate().apply(project, promptForOverwrite);
	}

	public IPath[] getIndexFiles()
	{
		// default behavior
		return null;
	}

	protected ProjectTemplate createProjectTemplate()
	{
		return new ProjectTemplate((new File(getDirectory(), getLocation())).getAbsolutePath(), getType(),
				getDisplayName(), isReplacingParameters(), getDescription(), getIconURL(), getId(), getPriority(),
				getTags());
	}

	public boolean isPrePackaged()
	{
		return false;
	}
}
