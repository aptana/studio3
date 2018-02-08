/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IPath;

import com.aptana.core.util.SourcePrinter;

public class ProjectSampleElement extends AbstractBundleElement
{

	private static final String DEFAULT_ICON = "default"; //$NON-NLS-1$

	private String fSampleId;
	private String fCategoryId;
	private String fLocation;
	private String fDescription;
	private String[] fProjectNatures;
	private IPath fDestinationPath;
	private Map<String, String> fIconPaths;
	private Map<String, URL> fIconUrls;

	/**
	 * @param path
	 */
	public ProjectSampleElement(String path)
	{
		super(path);
		fIconPaths = new HashMap<String, String>();
		fIconUrls = new HashMap<String, URL>();
	}

	/**
	 * @param sourcePath
	 * @param id
	 * @param displayName
	 * @param categoryId
	 * @param description
	 * @param icon
	 * @param destinationPath
	 */
	public ProjectSampleElement(String sourcePath, String id, String displayName, String categoryId,
			String description, URL icon, IPath destinationPath, String[] natures)
	{
		super(sourcePath);
		fLocation = sourcePath;
		fSampleId = id;
		fCategoryId = categoryId;
		fDescription = description;
		fDestinationPath = destinationPath;
		fProjectNatures = natures;
		fIconPaths = new HashMap<String, String>();
		fIconUrls = new HashMap<String, URL>();
		setDisplayName(displayName);
		setIcon(DEFAULT_ICON, icon.toExternalForm());
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

	public Map<String, String> getIcon()
	{
		return fIconPaths;
	}

	public Map<String, URL> getIconUrls()
	{
		Map<String, String> iconPaths = getIcon();
		Collection<String> iconSizes = iconPaths.keySet();
		for (String size : iconSizes)
		{
			String iconPath = iconPaths.get(size);
			if (iconPath == null)
			{
				continue;
			}

			URL iconUrl = fIconUrls.get(size);
			if (iconUrl == null)
			{
				iconUrl = getURLFromPath(iconPath);
				fIconUrls.put(size, iconUrl);
			}
		}
		return fIconUrls;
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

	public IPath getDestinationPath()
	{
		return fDestinationPath;
	}

	public void setIcon(String defaultIcon)
	{
		setIcon(DEFAULT_ICON, defaultIcon);
	}

	public void setIcon(Map<String, String> iconMap)
	{
		Collection<String> iconSizes = iconMap.keySet();
		for (String size : iconSizes)
		{
			setIcon(size, iconMap.get(size));
		}
	}

	public void setIcon(String iconSize, String iconPath)
	{
		fIconPaths.put(iconSize, iconPath);
		fIconUrls.remove(iconSize);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof ProjectSampleElement))
		{
			return false;
		}
		ProjectSampleElement other = (ProjectSampleElement) obj;
		return getCategory().equals(other.getCategory()) && getId().equals(other.getId())
				&& getLocation().equals(other.getLocation());
	}

	@Override
	public int hashCode()
	{
		int hash = getCategory().hashCode();
		hash = 31 * hash + getId().hashCode();
		hash = 31 * hash + getLocation().hashCode();
		return hash;
	}

	@Override
	protected void printBody(SourcePrinter printer, boolean includeBlocks)
	{
		printer.printWithIndent("id: ").println(getId()); //$NON-NLS-1$
		printer.printlnWithIndent("category: ").println(getCategory()); //$NON-NLS-1$
		printer.printWithIndent("name: ").println(getDisplayName()); //$NON-NLS-1$
		printer.printWithIndent("location: ").println(getLocation()); //$NON-NLS-1$

		String description = getDescription();
		if (description != null)
		{
			printer.printWithIndent("description: ").println(description); //$NON-NLS-1$
		}
		String[] natures = getNatures();
		if (natures != null)
		{
			int count = natures.length;
			if (count > 0)
			{
				StringBuilder natureStr = new StringBuilder();
				natureStr.append('[');
				for (int i = 0; i < count; ++i)
				{
					natureStr.append(natures[i]);
					if (i < count - 1)
					{
						natureStr.append(", "); //$NON-NLS-1$
					}
				}
				natureStr.append(']');
				printer.printWithIndent("natures: ").println(natureStr.toString()); //$NON-NLS-1$
			}
		}
		Map<String, URL> iconUrls = getIconUrls();
		if (iconUrls.size() > 0)
		{
			printer.printWithIndent("iconURL: "); //$NON-NLS-1$
			StringBuilder text = new StringBuilder();
			Collection<String> iconSizes = iconUrls.keySet();
			for (String size : iconSizes)
			{
				text.append(size).append('=').append(iconUrls.get(size)).append(',');
			}
			text.deleteCharAt(text.length() - 1);
			printer.println(text.toString());
		}
	}
}
