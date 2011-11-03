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

public class ProjectSampleElement extends AbstractBundleElement
{

	private String fSampleId;
	private String fCategoryId;
	private String fLocation;
	private String fDescription;
	private String[] fProjectNatures;

	/**
	 * @param path
	 */
	public ProjectSampleElement(String path)
	{
		super(path);
	}

	/**
	 * @return the id of the sample
	 */
	public String getId()
	{
		return fSampleId;
	}

	/**
	 * @return the id of the category the sample belongs in
	 */
	public String getCategory()
	{
		return fCategoryId;
	}

	/**
	 * @return a remote git url or a local zip location
	 */
	public String getLocation()
	{
		return fLocation;
	}

	/**
	 * @return the description of the sample
	 */
	public String getDescription()
	{
		return fDescription;
	}

	public String[] getNatures()
	{
		return fProjectNatures;
	}

	/**
	 * @return the directory of the bundle
	 */
	public File getDirectory()
	{
		return this.getOwningBundle().getBundleDirectory();
	}

	@Override
	public String getElementName()
	{
		return "project_sample"; //$NON-NLS-1$
	}

	/**
	 * @param id
	 *            the id of the sample
	 */
	public void setId(String id)
	{
		fSampleId = id;
	}

	/**
	 * @param description
	 *            the description of the sample
	 */
	public void setDescription(String description)
	{
		fDescription = description;
	}

	/**
	 * @param location
	 *            a remote git url or a local zip location
	 */
	public void setLocation(String location)
	{
		fLocation = location;
	}

	/**
	 * @param categoryId
	 *            the id of the category the sample belongs in
	 */
	public void setCategory(String categoryId)
	{
		fCategoryId = categoryId;
	}

	public void setNatures(String[] natures)
	{
		fProjectNatures = natures;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof ProjectSampleElement))
		{
			return false;
		}
		ProjectSampleElement other = (ProjectSampleElement) obj;
		return getCategory().equals(other.getCategory()) && getDisplayName().equals(other.getDisplayName())
				&& getLocation().equals(other.getLocation());
	}

	@Override
	public int hashCode()
	{
		int hash = getCategory().hashCode();
		hash = 31 * hash + getDisplayName().hashCode();
		hash = 31 * hash + getLocation().hashCode();
		return hash;
	}

	@Override
	protected void printBody(SourcePrinter printer, boolean includeBlocks)
	{
		printer.printWithIndent("path: ").println(this.getPath()); //$NON-NLS-1$
		printer.printWithIndent("name: ").println(this.getDisplayName()); //$NON-NLS-1$
		printer.printWithIndent("location: ").println(this.getLocation()); //$NON-NLS-1$

		if (this.getDescription() != null)
		{
			printer.printWithIndent("description: ").println(this.getDescription()); //$NON-NLS-1$
		}
	}
}
