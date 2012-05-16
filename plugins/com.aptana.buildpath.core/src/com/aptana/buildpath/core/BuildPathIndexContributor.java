/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.buildpath.core;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ArrayUtil;
import com.aptana.index.core.IIndexFileContributor;

public class BuildPathIndexContributor implements IIndexFileContributor
{
	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.IIndexFileContributor#contributeFiles(java.net.URI)
	 */
	public Set<IFileStore> getFiles(URI containerURI)
	{
		IContainer[] containers = ResourcesPlugin.getWorkspace().getRoot().findContainersForLocationURI(containerURI);

		if (ArrayUtil.isEmpty(containers))
		{
			return Collections.emptySet();
		}

		Set<IFileStore> result = new HashSet<IFileStore>();
		for (IContainer container : containers)
		{
			if (container instanceof IProject)
			{
				IProject project = (IProject) container;
				Set<BuildPathEntry> entries = BuildPathManager.getInstance().getBuildPaths(project);

				if (entries != null)
				{
					for (BuildPathEntry entry : entries)
					{
						try
						{
							IFileStore fileStore = EFS.getStore(entry.getPath());

							result.add(fileStore);
						}
						catch (CoreException e)
						{
							IdeLog.logError(BuildPathCorePlugin.getDefault(), e);
						}
					}
				}
			}
		}

		return result;
	}
}
