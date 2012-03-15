/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.buildpath.core;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.osgi.framework.Bundle;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.ConfigurationElementDispatcher;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IConfigurationElementProcessor;
import com.aptana.core.util.ResourceUtil;
import com.aptana.core.util.StringUtil;

/**
 * BuildPathManager
 */
public class BuildPathManager
{
	private static final String PROJECT_BUILD_PATH_PROPERTY_NAME = "projectBuildPath"; //$NON-NLS-1$
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

	private Set<BuildPathEntry> buildPaths;
	private List<IBuildPathContributor> contributors;

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
				buildPaths = new HashSet<BuildPathEntry>();
			}

			buildPaths.add(entry);
		}
	}

	/**
	 * addBuildPath
	 * 
	 * @param project
	 * @param entry
	 */
	public void addBuildPath(IProject project, BuildPathEntry entry)
	{
		if (project != null && entry != null)
		{
			Set<BuildPathEntry> buildPaths = getBuildPaths(project);

			if (!buildPaths.contains(entry))
			{
				buildPaths.add(entry);

				setBuildPaths(project, buildPaths);
			}
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
	 * Add a dynamic file contributor
	 * 
	 * @param contributor
	 */
	protected void addContributor(IBuildPathContributor contributor)
	{
		if (contributor != null)
		{
			if (contributors == null)
			{
				contributors = new ArrayList<IBuildPathContributor>();
			}

			contributors.add(contributor);
		}
	}

	/**
	 * getBuildPathPropertyName
	 * 
	 * @return
	 */
	protected QualifiedName getBuildPathPropertyName()
	{
		return new QualifiedName(BuildPathCorePlugin.PLUGIN_ID, PROJECT_BUILD_PATH_PROPERTY_NAME);
	}

	/**
	 * getBuildPaths
	 * 
	 * @return
	 */
	public Set<BuildPathEntry> getBuildPaths()
	{
		Set<BuildPathEntry> result = new HashSet<BuildPathEntry>();

		// Add static paths, if we have any
		if (buildPaths != null)
		{
			result.addAll(buildPaths);
		}

		// Add dynamic paths
		result.addAll(getDynamicBuildPaths());

		return result;
	}

	/**
	 * getBuildPaths
	 * 
	 * @param project
	 * @return
	 */
	public Set<BuildPathEntry> getBuildPaths(IProject project)
	{
		Set<BuildPathEntry> result = new HashSet<BuildPathEntry>();

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
						String uri = nameAndPath[1];

						try
						{
							URI path = new URI(uri);

							result.add(new BuildPathEntry(name, path));
						}
						catch (URISyntaxException e)
						{
							// @formatter:off
							String message = MessageFormat.format(
								Messages.BuildPathManager_UnableToConvertURI,
								uri,
								PROJECT_BUILD_PATH_PROPERTY_NAME,
								project.getName()
							);
							// @formatter:on

							IdeLog.logError(BuildPathCorePlugin.getDefault(), message, e);
						}
					}
				}
			}
		}
		catch (CoreException e)
		{
			// @formatter:off
			String message = MessageFormat.format(
				Messages.BuildPathManager_UnableToRetrievePersistenceProperty,
				PROJECT_BUILD_PATH_PROPERTY_NAME,
				project.getName()
			);
			// @formatter:on

			IdeLog.logError(BuildPathCorePlugin.getDefault(), message, e);
		}

		if (!result.isEmpty())
		{
			// only include paths that are actually registered
			result.retainAll(getBuildPaths());
		}

		return result;
	}

	/**
	 * getDynamicBuildPaths
	 * 
	 * @return
	 */
	private Set<BuildPathEntry> getDynamicBuildPaths()
	{
		Set<BuildPathEntry> result;

		if (contributors != null)
		{
			result = new HashSet<BuildPathEntry>();

			for (IBuildPathContributor contributor : contributors)
			{
				List<BuildPathEntry> files = contributor.getBuildPathEntries();

				if (files != null)
				{
					result.addAll(files);
				}
			}
		}
		else
		{
			result = Collections.emptySet();
		}

		return result;
	}

	/**
	 * hasBuildPath
	 * 
	 * @param entry
	 * @return
	 */
	public boolean hasBuildPath(BuildPathEntry entry)
	{
		return getBuildPaths().contains(entry);
	}

	/**
	 * hasBuildPath
	 * 
	 * @param project
	 * @param entry
	 * @return
	 */
	public boolean hasBuildPath(IProject project, BuildPathEntry entry)
	{
		boolean result = false;

		if (project != null && entry != null)
		{
			Set<BuildPathEntry> buildPathSet = getBuildPaths(project);

			result = buildPathSet.contains(entry);
		}

		return result;
	}

	private class BuildPathProcessor implements IConfigurationElementProcessor
	{

		/*
		 * (non-Javadoc)
		 * @see
		 * com.aptana.core.util.IConfigurationElementProcessor#processElement(org.eclipse.core.runtime.IConfigurationElement
		 * )
		 */
		public void processElement(IConfigurationElement element)
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

			// add item to master list
			URI localFileURI = ResourceUtil.resourcePathToURI(url);

			if (localFileURI != null)
			{
				addBuildPath(name, localFileURI);
			}
			else
			{
				// @formatter:off
				String message = MessageFormat.format(
					Messages.BuildPathManager_UnableToConvertURLToURI,
					url.toString(),
					ELEMENT_BUILD_PATH,
					BUILD_PATHS_ID,
					pluginId
				);
				// @formatter:on

				IdeLog.logError(BuildPathCorePlugin.getDefault(), message);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.core.util.IConfigurationElementProcessor#getSupportElementNames()
		 */
		public Set<String> getSupportElementNames()
		{
			return CollectionsUtil.newSet(ELEMENT_BUILD_PATH);
		}
	}

	private class ContributorProcessor implements IConfigurationElementProcessor
	{

		/*
		 * (non-Javadoc)
		 * @see
		 * com.aptana.core.util.IConfigurationElementProcessor#processElement(org.eclipse.core.runtime.IConfigurationElement
		 * )
		 */
		public void processElement(IConfigurationElement element)
		{
			try
			{
				Object contributor = element.createExecutableExtension(ATTR_CLASS);

				if (contributor instanceof IBuildPathContributor)
				{
					addContributor((IBuildPathContributor) contributor);
				}
				else
				{
					IExtension extension = element.getDeclaringExtension();
					String pluginId = extension.getNamespaceIdentifier();

					// @formatter:off
					String message = MessageFormat.format(
						Messages.BuildPathManager_PathContributorIsWrongType,
						contributor.getClass().getName(),
						ELEMENT_CONTRIBUTOR,
						BUILD_PATHS_ID,
						pluginId
					);
					// @formatter:on

					IdeLog.logError(BuildPathCorePlugin.getDefault(), message);
				}
			}
			catch (CoreException e)
			{
				IExtension extension = element.getDeclaringExtension();
				String pluginId = extension.getNamespaceIdentifier();
				// @formatter:off
				String message = MessageFormat.format(
					Messages.BuildPathManager_UnableToCreatePathContributor,
					ELEMENT_CONTRIBUTOR,
					BUILD_PATHS_ID,
					pluginId
				);
				// @formatter:on

				IdeLog.logError(BuildPathCorePlugin.getDefault(), message);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see com.aptana.core.util.IConfigurationElementProcessor#getSupportElementNames()
		 */
		public Set<String> getSupportElementNames()
		{
			return CollectionsUtil.newSet(ELEMENT_CONTRIBUTOR);
		}
	}

	/**
	 * Process all load path extensions
	 */
	private void loadExtension()
	{
		// @formatter:off
		// configure dispatcher for each element type we process
		ConfigurationElementDispatcher dispatcher = new ConfigurationElementDispatcher(
				new BuildPathProcessor(),
				new ContributorProcessor()
			);
		
		EclipseUtil.processConfigurationElements(
			BuildPathCorePlugin.PLUGIN_ID,
			BUILD_PATHS_ID,
			dispatcher
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
	 * removeBuildPath
	 * 
	 * @param project
	 * @param entry
	 */
	public void removeBuildPath(IProject project, BuildPathEntry entry)
	{
		if (project != null && entry != null)
		{
			Set<BuildPathEntry> entries = getBuildPaths(project);

			if (entries.contains(entry))
			{
				entries.remove(entry);

				setBuildPaths(project, entries);
			}
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
	 * setBuildPaths
	 * 
	 * @param project
	 * @param entries
	 */
	public void setBuildPaths(IProject project, Collection<BuildPathEntry> entries)
	{
		if (project != null && entries != null)
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
				// @formatter:off
				String message = MessageFormat.format(
					Messages.BuildPathManager_UnableToSetPersistenceProperty,
					PROJECT_BUILD_PATH_PROPERTY_NAME,
					project.getName()
				);
				// @formatter:on

				IdeLog.logError(BuildPathCorePlugin.getDefault(), message, e);
			}
		}
	}
}
