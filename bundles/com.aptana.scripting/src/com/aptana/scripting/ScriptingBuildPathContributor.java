/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;

import com.aptana.buildpath.core.BuildPathEntry;
import com.aptana.buildpath.core.IBuildPathContributor;
import com.aptana.buildpath.core.IBuildPathEntry;
import com.aptana.scripting.model.BuildPathElement;
import com.aptana.scripting.model.BundleEntry;
import com.aptana.scripting.model.BundleManager;

/**
 * ScriptingBuildPathContributor
 */
public class ScriptingBuildPathContributor implements IBuildPathContributor
{
	/*
	 * (non-Javadoc)
	 * @see com.aptana.buildpath.core.IBuildPathContributor#contribute()
	 */
	public List<IBuildPathEntry> getBuildPathEntries()
	{
		BundleManager manager = BundleManager.getInstance();
		Set<IBuildPathEntry> result = new HashSet<IBuildPathEntry>();

		for (String name : manager.getBundleNames())
		{
			BundleEntry entry = manager.getBundleEntry(name);

			for (BuildPathElement element : entry.getBuildPaths())
			{
				File file = new File(element.getBuildPath());
				BuildPathEntry buildPath = new BuildPathEntry(element.getDisplayName(), file.toURI());

				result.add(buildPath);
			}
		}

		return new ArrayList<IBuildPathEntry>(result);
	}

	public List<IBuildPathEntry> getBuildPathEntries(IProject project)
	{
		return Collections.emptyList();
	}
}
