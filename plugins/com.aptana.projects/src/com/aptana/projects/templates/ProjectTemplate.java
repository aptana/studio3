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

import com.aptana.core.projects.templates.IProjectTemplate;
import com.aptana.core.projects.templates.TemplateType;
import com.aptana.core.util.SourcePrinter;
import com.aptana.scripting.model.ProjectTemplateElement;

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
	private String id;
	private URL iconPath;
	private boolean isReplacingParameters;

	/**
	 * Constructs a new ProjectTemplate
	 * 
	 * @param path
	 * @param type
	 * @param name
	 * @param isReplacingParameters
	 * @param description
	 * @param iconPath
	 * @param id
	 */
	public ProjectTemplate(String path, TemplateType type, String name, boolean isReplacingParameters,
			String description, URL iconPath, String id)
	{
		this.type = type;
		this.path = path;
		this.name = name;
		this.isReplacingParameters = isReplacingParameters;
		this.description = description;
		this.iconPath = iconPath;
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.projects.templates.IProjectTemplate#getDisplayName()
	 */
	public String getDisplayName()
	{
		return name;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.projects.templates.IProjectTemplate#getDescription()
	 */
	public String getDescription()
	{
		return description;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.projects.templates.IProjectTemplate#getDirectory()
	 */
	public File getDirectory()
	{
		return new File(path).getParentFile();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.projects.templates.IProjectTemplate#getLocation()
	 */
	public String getLocation()
	{
		return Path.fromOSString(path).lastSegment();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.projects.templates.IProjectTemplate#getType()
	 */
	public TemplateType getType()
	{
		return type;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.projects.templates.IProjectTemplate#getIconPath()
	 */
	public URL getIconPath()
	{
		return iconPath;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.projects.templates.IProjectTemplate#isReplacingParameters()
	 */
	public boolean isReplacingParameters()
	{
		return isReplacingParameters;
	}

	protected void toSource(SourcePrinter printer)
	{
	}

	@Override
	public String toString()
	{
		SourcePrinter printer = new SourcePrinter();

		// open element
		printer.printWithIndent("project_template"); //$NON-NLS-1$
		printer.print(" \"").print(this.getDisplayName()).println("\" {").increaseIndent(); //$NON-NLS-1$ //$NON-NLS-2$

		ProjectTemplateElement.printBody(printer, false, this);

		// close element
		printer.decreaseIndent().printlnWithIndent("}"); //$NON-NLS-1$
		return printer.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.projects.templates.IProjectTemplate#getId()
	 */
	public String getId()
	{
		return id;
	}

	public String getPath()
	{
		return path;
	}
}
