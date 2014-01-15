/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
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
import com.aptana.index.core.IndexContainerJob;
import com.aptana.index.core.IndexFileJob;

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
				buildPaths = new LinkedHashSet<BuildPathEntry>();
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
	public void addBuildPath(IProject project, IBuildPathEntry entry)
	{
		if (project != null && entry != null)
		{
			Set<IBuildPathEntry> buildPaths = getBuildPaths(project);

			if (!buildPaths.contains(entry))
			{
				buildPaths.add(entry);

				setBuildPaths(project, buildPaths);

				index(entry);
			}
		}
	}

	/**
	 * If a build path entry is added, we schedule a job to make sure the entry gets indexed (or it's index is
	 * up-to-date).
	 * 
	 * @param entry
	 */
	private void index(IBuildPathEntry entry)
	{
		try
		{
			IFileStore fileStore = EFS.getStore(entry.getPath());
			if (fileStore != null)
			{
				if (fileStore.fetchInfo().isDirectory())
				{
					new IndexContainerJob(entry.getDisplayName(), entry.getPath()).schedule();
				}
				else
				{
					new IndexFileJob(entry.getDisplayName(), entry.getPath()).schedule();
				}
			}
		}
		catch (Throwable e)
		{
			IdeLog.logError(BuildPathCorePlugin.getDefault(), e);
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
	public Set<IBuildPathEntry> getBuildPaths()
	{
		Set<IBuildPathEntry> result = new LinkedHashSet<IBuildPathEntry>();

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
	// FIXME We need this to be a list, because order matters!
	public Set<IBuildPathEntry> getBuildPaths(IProject project)
	{
		if (project == null)
		{
			return Collections.emptySet();
		}

		Set<IBuildPathEntry> result = new LinkedHashSet<IBuildPathEntry>();
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
	private Set<IBuildPathEntry> getDynamicBuildPaths()
	{
		Set<IBuildPathEntry> result;

		if (contributors != null)
		{
			result = new LinkedHashSet<IBuildPathEntry>();

			for (IBuildPathContributor contributor : contributors)
			{
				List<IBuildPathEntry> files = contributor.getBuildPathEntries();

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
	public boolean hasBuildPath(IProject project, IBuildPathEntry entry)
	{
		boolean result = false;

		if (project != null && entry != null)
		{
			Set<IBuildPathEntry> buildPathSet = getBuildPaths(project);

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
	public void removeBuildPath(IProject project, IBuildPathEntry entry)
	{
		if (project != null && entry != null)
		{
			Set<IBuildPathEntry> entries = getBuildPaths(project);

			if (entries.contains(entry))
			{
				entries.remove(entry);

				setBuildPaths(project, entries);

				// TODO Remove the index for it? Don't we need to see if no other references to it are out there?
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
	public void setBuildPaths(IProject project, Collection<IBuildPathEntry> entries)
	{
		if (project != null && entries != null)
		{
			List<String> nameAndPaths = new ArrayList<String>();

			for (IBuildPathEntry entry : entries)
			{
				String nameAndPath = entry.getDisplayName() + NAME_AND_PATH_DELIMITER + entry.getPath();

				nameAndPaths.add(nameAndPath);
			}

			String value = StringUtil.join(BUILD_PATH_ENTRY_DELIMITER, nameAndPaths);

			// FIXME This severely limits the value's size, which we could run into over time! It also does not make the
			// value portable across users/workspaces
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
