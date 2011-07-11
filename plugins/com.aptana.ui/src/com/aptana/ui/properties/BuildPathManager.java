/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;

import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IConfigurationElementProcessor;
import com.aptana.core.util.StringUtil;
import com.aptana.ui.UIPlugin;

/**
 * BuildPathManager
 */
public class BuildPathManager
{
	private static final String BUILD_PATHS_ID = "buildPaths"; //$NON-NLS-1$
	private static final String ELEMENT_BUILD_PATH = "buildPath"; //$NON-NLS-1$
	private static final String ATTR_NAME = "name"; //$NON-NLS-1$
	private static final String ATTR_PATH = "path"; //$NON-NLS-1$

	private static BuildPathManager instance;

	private List<BuildPathEntry> buildPaths;

	/**
	 * Make sure this is a singleton
	 */
	private BuildPathManager()
	{
	}

	/**
	 * Return the singleton instance of this class
	 * 
	 * @return
	 */
	public static synchronized BuildPathManager getInstance()
	{
		if (instance == null)
		{
			instance = new BuildPathManager();

			instance.loadExtension();
		}

		return instance;
	}

	/**
	 * Add a new build path entry to this manager
	 * 
	 * @param displayName
	 * @param path
	 */
	public void addBuildPath(String displayName, String path)
	{
		if (!StringUtil.isEmpty(displayName) && !StringUtil.isEmpty(path))
		{
			addBuildPath(new BuildPathEntry(displayName, path));
		}
	}

	/**
	 * Add a new build path entry to this manager
	 * 
	 * @param entry
	 */
	public void addBuildPath(BuildPathEntry entry)
	{
		if (entry != null)
		{
			if (buildPaths == null)
			{
				buildPaths = new ArrayList<BuildPathEntry>();
			}

			buildPaths.add(entry);
		}
	}

	/**
	 * Remove an existing build path from this manager
	 * 
	 * @param displayName
	 * @param path
	 */
	public void removeBuildPath(String displayName, String path)
	{
		if (!StringUtil.isEmpty(displayName) && !StringUtil.isEmpty(path))
		{
			removeBuildPath(new BuildPathEntry(displayName, path));
		}
	}

	/**
	 * Remove an existing build path from this manager
	 * 
	 * @param entry
	 */
	public void removeBuildPath(BuildPathEntry entry)
	{
		if (entry != null && buildPaths != null)
		{
			buildPaths.remove(entry);
		}
	}

	/**
	 * getBuildPaths
	 * 
	 * @return
	 */
	public List<BuildPathEntry> getBuildPaths()
	{
		List<BuildPathEntry> result = Collections.emptyList();

		if (buildPaths != null)
		{
			result = buildPaths;
		}

		return result;
	}

	/**
	 * Process all load path extensions
	 */
	private void loadExtension()
	{
		// @formatter:off
		EclipseUtil.processConfigurationElements(
			UIPlugin.PLUGIN_ID,
			BUILD_PATHS_ID,
			ELEMENT_BUILD_PATH,
			new IConfigurationElementProcessor()
			{
				public void processElement(IConfigurationElement element)
				{
					String name = element.getAttribute(ATTR_NAME);
					String path = element.getAttribute(ATTR_PATH);
					
					addBuildPath(name, path);
				}
			}
		);
		// @formatter:on
	}
}
