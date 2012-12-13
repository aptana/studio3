/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.inferencing;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * Resolves a module id relative to a base module namespace root, ore if the path is explicitly relative, relative to
 * current location.
 * 
 * @author cwilliams
 */
public class CommonJSResolver implements IRequireResolver
{

	public IPath resolve(String moduleId, IProject project, IPath currentLocation, IPath indexRoot)
	{
		if (!currentLocation.toFile().isDirectory())
		{
			throw new IllegalArgumentException("current location must be a directory"); //$NON-NLS-1$
		}
		if (!indexRoot.toFile().isDirectory())
		{
			throw new IllegalArgumentException("module namespace root must be a directory"); //$NON-NLS-1$
		}

		IPath modulePath = Path.fromPortableString(moduleId);
		if (modulePath.getFileExtension() == null)
		{
			modulePath = modulePath.addFileExtension("js"); //$NON-NLS-1$
		}
		if (moduleId.startsWith(".")) //$NON-NLS-1$
		{
			// relative
			return currentLocation.append(modulePath);
		}
		// absolute, so resolve releative to index root
		return indexRoot.append(modulePath);
	}

	public boolean applies(IProject project, IPath currentDirectory, IPath indexRoot)
	{
		// CommonJS will be our fallback. Should always apply.
		return true;
	}

}
