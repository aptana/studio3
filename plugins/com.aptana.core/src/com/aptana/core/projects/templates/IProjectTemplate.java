/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.projects.templates;

import java.io.File;
import java.net.URL;

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
	public URL getIconPath();

	/**
	 * Returns true if the template should evaluate and substitute the template-tags when imported to the workspace.
	 * 
	 * @return True, if tags-substitution should occur when importing; False, otherwise.
	 */
	public boolean isReplacingParameters();
}
