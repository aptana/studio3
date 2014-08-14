/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.inferencing;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

class NullRequireResolver implements IRequireResolver
{
	public IPath resolve(String moduleId, IProject project, IPath currentDirectory, IPath indexRoot)
	{
		return null;
	}

	public boolean applies(IProject project, IPath currentDirectory, IPath indexRoot)
	{
		return true;
	}

	public List<String> getPossibleModuleIds(IProject project, IPath currentDirectory, IPath indexRoot)
	{
		return Collections.emptyList();
	}
}
