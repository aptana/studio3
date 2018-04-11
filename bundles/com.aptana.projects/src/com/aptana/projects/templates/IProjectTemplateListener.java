/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.templates;

import com.aptana.core.projects.templates.IProjectTemplate;

public interface IProjectTemplateListener
{

	/**
	 * Fired when a project template is added.
	 * 
	 * @param template
	 *            the new project template
	 */
	public void templateAdded(IProjectTemplate template);

	/**
	 * Fired when a project template is deleted.
	 * 
	 * @param template
	 *            the deleted project template
	 */
	public void templateRemoved(IProjectTemplate template);
}
