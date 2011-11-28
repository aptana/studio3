/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.projects.templates.IProjectTemplate;
import com.aptana.core.projects.templates.TemplateType;
import com.aptana.core.util.SourcePrinter;
import com.aptana.core.util.StringUtil;
import com.aptana.scripting.ScriptingActivator;

public class ProjectTemplateElement extends AbstractBundleElement implements IProjectTemplate
{
	private TemplateType fType = TemplateType.UNDEFINED;
	private String fLocation;
	private String fDescription;
	private String fId;
	private String fIconPath;
	private URL fIconURL;

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
		printBody(printer, includeBlocks, this);
	}

	/**
	 * Prints the interior body of the template element
	 * 
	 * @param printer
	 * @param includeBlocks
	 * @param template
	 */
	public static void printBody(SourcePrinter printer, boolean includeBlocks, IProjectTemplate template)
	{
		printer.printWithIndent("path: ").println(template.getPath()); //$NON-NLS-1$
		printer.printWithIndent("name: ").println(template.getDisplayName()); //$NON-NLS-1$
		printer.printWithIndent("location: ").println(template.getLocation()); //$NON-NLS-1$
		printer.printWithIndent("id: ").println(template.getId()); //$NON-NLS-1$
		printer.printWithIndent("type: ").println(template.getType().name()); //$NON-NLS-1$
		printer.printWithIndent("replaceParameters: ").println(Boolean.toString(template.isReplacingParameters())); //$NON-NLS-1$

		if (template.getDescription() != null)
		{
			printer.printWithIndent("description: ").println(template.getDescription()); //$NON-NLS-1$
		}

		if (template.getIconURL() != null)
		{
			printer.printWithIndent("iconURL: ").println(template.getIconURL().toString()); //$NON-NLS-1$
		}

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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.projects.templates.IProjectTemplate#getIconPath()
	 */
	public URL getIconURL()
	{
		if (fIconPath == null)
		{
			return null;
		}

		if (fIconURL != null)
		{
			return fIconURL;
		}

		try
		{
			// First try to convert path into a URL
			fIconURL = new URL(fIconPath);
		}
		catch (MalformedURLException e1)
		{
			// If it fails, assume it's a project-relative local path
			IPath path = new Path(getDirectory().getAbsolutePath()).append(fIconPath);
			try
			{
				fIconURL = path.toFile().toURI().toURL();
			}
			catch (Exception e)
			{
				IdeLog.logError(ScriptingActivator.getDefault(), MessageFormat.format(
						"Unable to convert {0} into an icon URL for template {1}", fIconPath, getDisplayName())); //$NON-NLS-1$
			}
		}

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
}
