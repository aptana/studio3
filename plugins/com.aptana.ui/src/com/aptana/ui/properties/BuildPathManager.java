/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.properties;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.osgi.framework.Bundle;

import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IConfigurationElementProcessor;
import com.aptana.core.util.StringUtil;
import com.aptana.ui.UIPlugin;

/**
 * BuildPathManager
 */
public class BuildPathManager
{
	private static final String NAME_AND_PATH_DELIMITER = "\t"; //$NON-NLS-1$
	private static final String BUILD_PATH_ENTRY_DELIMITER = "\0"; //$NON-NLS-1$

	private static final String BUILD_PATHS_ID = "buildPaths"; //$NON-NLS-1$
	private static final String ELEMENT_BUILD_PATH = "buildPath"; //$NON-NLS-1$
	private static final String ELEMENT_CONTRIBUTOR = "contributor"; //$NON-NLS-1$
	private static final String ATTR_NAME = "name"; //$NON-NLS-1$
	private static final String ATTR_PATH = "path"; //$NON-NLS-1$
	private static final String ATTR_CLASS = "class"; //$NON-NLS-1$

	private static BuildPathManager instance;

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

	private List<BuildPathEntry> buildPaths;

	/**
	 * Make sure this is a singleton
	 */
	private BuildPathManager()
	{
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
	 * Add a new build path entry to this manager
	 * 
	 * @param displayName
	 * @param path
	 */
	public void addBuildPath(String displayName, URI path)
	{
		if (!StringUtil.isEmpty(displayName) && path != null)
		{
			addBuildPath(new BuildPathEntry(displayName, path));
		}
	}

	/**
	 * getBuildPathPropertyName
	 * 
	 * @return
	 */
	protected QualifiedName getBuildPathPropertyName()
	{
		return new QualifiedName(UIPlugin.PLUGIN_ID, "projectBuildPath");
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
	 * getSelectedBuildPathEntries
	 * 
	 * @param project
	 * @return
	 */
	public List<BuildPathEntry> getSelectedBuildPathEntries(IProject project)
	{
		List<BuildPathEntry> result = new ArrayList<BuildPathEntry>();

		try
		{
			String property = project.getPersistentProperty(getBuildPathPropertyName());

			if (property != null)
			{
				String[] entries = property.split(BUILD_PATH_ENTRY_DELIMITER);

				for (String entry : entries)
				{
					String[] nameAndPath = entry.split(NAME_AND_PATH_DELIMITER);

					if (nameAndPath.length >= 2)
					{
						String name = nameAndPath[0];

						try
						{
							URI path = new URI(nameAndPath[1]);

							result.add(new BuildPathEntry(name, path));
						}
						catch (URISyntaxException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
		catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			new IConfigurationElementProcessor()
			{
				public void processElement(IConfigurationElement element)
				{
					try
					{
						if (ELEMENT_BUILD_PATH.equals(element.getName()))
						{
							// get extension pt's bundle
							IExtension extension = element.getDeclaringExtension();
							String pluginId = extension.getNamespaceIdentifier();
							Bundle bundle = Platform.getBundle(pluginId);

							// grab the item's display name
							String name = element.getAttribute(ATTR_NAME);

							// get the item's URI, resolved to a local file
							String resource = element.getAttribute(ATTR_PATH);
							URL url = FileLocator.find(bundle, new Path(resource), null);
							url = FileLocator.resolve(url);

							// add item to master list
							addBuildPath(name, url.toURI());
						}
						else if (ELEMENT_CONTRIBUTOR.equals(element.getName()))
						{
							IBuildPathContributor contributor = (IBuildPathContributor) element.createExecutableExtension(ATTR_CLASS);
							List<BuildPathEntry> entries = contributor.contribute();

							if (entries != null)
							{
								for (BuildPathEntry entry : entries)
								{
									addBuildPath(entry);
								}
							}
						}
					}
					catch (URISyntaxException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					catch (CoreException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			},
			ELEMENT_BUILD_PATH,
			ELEMENT_CONTRIBUTOR
		);
		// @formatter:on
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
	 * Remove an existing build path from this manager
	 * 
	 * @param displayName
	 * @param path
	 */
	public void removeBuildPath(String displayName, URI path)
	{
		if (!StringUtil.isEmpty(displayName) && path != null)
		{
			removeBuildPath(new BuildPathEntry(displayName, path));
		}
	}

	/**
	 * setSelectedBuildPathEntries
	 * 
	 * @param project
	 * @param entries
	 */
	public void setSelectedBuildPathEntries(IProject project, List<BuildPathEntry> entries)
	{
		List<String> nameAndPaths = new ArrayList<String>();

		for (BuildPathEntry entry : entries)
		{
			String nameAndPath = entry.getDisplayName() + NAME_AND_PATH_DELIMITER + entry.getPath();

			nameAndPaths.add(nameAndPath);
		}

		String value = StringUtil.join(BUILD_PATH_ENTRY_DELIMITER, nameAndPaths);

		try
		{
			project.setPersistentProperty(getBuildPathPropertyName(), value);
		}
		catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
