/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import java.io.File;

import com.aptana.core.util.SourcePrinter;

public class ProjectTemplateElement extends AbstractBundleElement
{
	public enum Type
	{
		UNDEFINED, ALL, RUBY, PHP, WEB, PYTHON
	}

	private Type fType = Type.UNDEFINED;
	private String fLocation;
	private String fDescription;

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
		return getType() == other.getType() && getDisplayName().equals(other.getDisplayName()) && getLocation().equals(other.getLocation());
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
	public Type getType()
	{
		return fType;
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
	protected void printBody(SourcePrinter printer)
	{
		printer.printWithIndent("path: ").println(this.getPath()); //$NON-NLS-1$
		printer.printWithIndent("name: ").println(this.getDisplayName()); //$NON-NLS-1$
		printer.printWithIndent("location: ").println(this.getLocation()); //$NON-NLS-1$

		if (this.getDescription() != null)
		{
			printer.printWithIndent("description: ").println(this.getDescription()); //$NON-NLS-1$
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
			fType = Type.valueOf(type.toUpperCase());
		}
		catch (Exception e)
		{
			fType = Type.UNDEFINED;
		}
	}
	
	/**
	 * setType
	 * 
	 * @param type
	 */
	public void setType(Type type)
	{
		fType = type;
	}
}
