/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.listeners;

import org.eclipse.core.resources.IProject;

public interface IStudioProjectChangeEvent
{
	/**
	 * Returns the project associated with the change event
	 * 
	 * @return
	 */
	IProject getProject();
}
