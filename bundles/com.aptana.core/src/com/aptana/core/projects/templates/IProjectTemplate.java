/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.projects.templates;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;

/**
 * A project template interface.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public interface IProjectTemplate
{

	/**
	 * getPath
	 * 
	 * @return
	 */
	public String getPath();

	/**
	 * Get the id associated with the template
	 * 
	 * @return
	 */
	public String getId();

	/**
	 * getDisplayName
	 * 
	 * @return
	 */
	public String getDisplayName();

	/**
	 * getDescription
	 * 
	 * @return
	 */
	public String getDescription();

	/**
	 * getDirectory
	 * 
	 * @return
	 */
	public File getDirectory();

	/**
	 * getLocation
	 * 
	 * @return
	 */
	public String getLocation();

	/**
	 * getType
	 * 
	 * @return
	 */
	public TemplateType getType();

	/**
	 * getIconPath
	 * 
	 * @return The template's icon path as URL (can be null)
	 */
	public URL getIconURL();

	/**
	 * @return the priority that indicates the order in which the templates should appear in a list
	 */
	public int getPriority();

	/**
	 * @return the list of tags
	 */
	public List<String> getTags();

	/**
	 * Returns true if the template should evaluate and substitute the template-tags when imported to the workspace.
	 * 
	 * @return True, if tags-substitution should occur when importing; False, otherwise.
	 */
	public boolean isReplacingParameters();

	/**
	 * Applies the template to a given project. Returns an error IStatus if there was an issue.
	 * 
	 * @param project
	 * @param promptForOverwrite
	 */
	public IStatus apply(IProject project, boolean promptForOverwrite);

	/**
	 * Returns the template's index files that will be opened when a project is created.
	 * 
	 * @return An {@link IPath} array of index files to open (can be <code>null</code>).
	 */
	public IPath[] getIndexFiles();

	/**
	 * @return True, if the template is pre-packaged as part of appc installation.
	 */
	public boolean isPrePackaged();
}
