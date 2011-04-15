/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.templates;

import java.io.File;
import java.net.URL;

import org.eclipse.core.runtime.Path;

import com.aptana.core.projectTemplates.IProjectTemplate;
import com.aptana.core.projectTemplates.TemplateType;

/**
 * Project template that is loaded from the <code>"projectTemplates"</code> extension point.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class ProjectTemplate implements IProjectTemplate
{

	private TemplateType type;
	private String path;
	private String description;
	private String name;
	private URL iconPath;

	/**
	 * Constructs a new ProjectTemplate
	 * 
	 * @param path
	 * @param type
	 * @param name
	 * @param description
	 * @param iconPath
	 */
	public ProjectTemplate(String path, TemplateType type, String name, String description, URL iconPath)
	{
		this.type = type;
		this.path = path;
		this.name = name;
		this.description = description;
		this.iconPath = iconPath;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.projectTemplates.IProjectTemplate#getDisplayName()
	 */
	public String getDisplayName()
	{
		return name;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.projectTemplates.IProjectTemplate#getDescription()
	 */
	public String getDescription()
	{
		return description;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.projectTemplates.IProjectTemplate#getDirectory()
	 */
	public File getDirectory()
	{
		return new File(path).getParentFile();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.projectTemplates.IProjectTemplate#getLocation()
	 */
	public String getLocation()
	{
		return Path.fromOSString(path).lastSegment();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.projectTemplates.IProjectTemplate#getType()
	 */
	public TemplateType getType()
	{
		return type;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.projectTemplates.IProjectTemplate#getIconPath()
	 */
	public URL getIconPath()
	{
		return iconPath;
	}
}
