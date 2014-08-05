/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.model;

import org.eclipse.core.resources.IProject;

import com.aptana.index.core.Index;
import com.aptana.index.core.IndexPlugin;

/**
 * JSElement
 */
public class JSElement extends BaseElement
{

	private IProject project;

	/**
	 * An element used to group JS content in an Index
	 * 
	 * @param project
	 *            The index that contains JS content
	 */
	public JSElement(IProject project)
	{
		this.project = project;
		setName(Messages.JSElement_NodeLabel);
	}

	/**
	 * Returns the element associated with this element
	 * 
	 * @return Returns an Index
	 */
	public Index getIndex()
	{
		return IndexPlugin.getDefault().getIndexManager().getIndex(project.getLocationURI());
	}

	public IProject getProject()
	{
		return project;
	}
}
