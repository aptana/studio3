/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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

	private Type fType;
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
		return "project_template";
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
		printer.printWithIndent("name: ").println(this.getDisplayName());
		printer.printWithIndent("location: ").println(this.getLocation());

		if (this.getDescription() != null)
		{
			printer.printWithIndent("description: ").println(this.getDescription());
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
}
