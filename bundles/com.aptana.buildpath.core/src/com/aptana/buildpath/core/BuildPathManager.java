/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.buildpath.core;

import java.net.URI;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
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

import com.aptana.core.IMap;
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
	/**
	 * cache the global list of available build paths
	 */
	private static final int CACHE_TIMEOUT = 30000;

	private static final String BUILD_PATH_ENTRY_DELIMITER = "\0"; //$NON-NLS-1$
	private static final String NAME_AND_PATH_DELIMITER = "\t"; //$NON-NLS-1$

	private static final String PROJECT_BUILD_PATH_PROPERTY_NAME = "projectBuildPath"; //$NON-NLS-1$

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

	private Set<IBuildPathEntry> whitelistedBuildPaths;
	private List<IBuildPathContributor> contributors;
	private long fTimestamp = -1;
	private LinkedHashSet<IBuildPathEntry> fBuildPaths;

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
	 * @return whether the entry was added or not.
	 */
	public synchronized boolean addBuildPath(IBuildPathEntry entry)
	{
		if (entry == null)
		{
			return false;
		}

		if (whitelistedBuildPaths == null)
		{
			// FIXME We need to synchronize on modifications and lazy init of this field!
			whitelistedBuildPaths = new LinkedHashSet<IBuildPathEntry>();
		}

		boolean result = whitelistedBuildPaths.add(entry);
		if (result)
		{
			fBuildPaths = null;
		}
		return result;
	}

	/**
	 * addBuildPath
	 * 
	 * @param project
	 * @param entry
	 * @return whether the entry was added or not. An entry may not get added because it already exists
	 */
	public boolean addBuildPath(IProject project, IBuildPathEntry entry)
	{
		if (project == null || entry == null)
		{
			return false;
		}

		// Use a copy, because we're going to modify
		Set<IBuildPathEntry> buildPaths = new HashSet<IBuildPathEntry>(getBuildPaths(project));
		if (buildPaths.add(entry))
		{
			setBuildPaths(project, buildPaths);
			index(entry);
			return true;
		}

		return false;
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
		catch (CoreException e)
		{
			IdeLog.logWarning(BuildPathCorePlugin.getDefault(), e);
		}
	}

	/**
	 * Add a new build path entry to this manager
	 * 
	 * @param displayName
	 * @param path
	 */
	private boolean addBuildPath(String displayName, URI path)
	{
		if (!StringUtil.isEmpty(displayName) && path != null)
		{
			return addBuildPath(new BuildPathEntry(displayName, path));
		}
		return false;
	}

	/**
	 * Add a dynamic file contributor
	 * 
	 * @param contributor
	 */
	private boolean addContributor(IBuildPathContributor contributor)
	{
		if (contributor == null)
		{
			return false;
		}

		if (contributors == null)
		{
			// FIXME need to synchronize on lazy init of field!
			contributors = new ArrayList<IBuildPathContributor>();
		}

		return contributors.add(contributor);
	}

	private QualifiedName getBuildPathPropertyName()
	{
		return new QualifiedName(BuildPathCorePlugin.PLUGIN_ID, PROJECT_BUILD_PATH_PROPERTY_NAME);
	}

	/**
	 * Returns the list of all possible valid build paths globally. Used primarily to display the list in the build path
	 * UI for users to add/remove manually for a given project.
	 * 
	 * @return
	 */
	public synchronized Set<IBuildPathEntry> getBuildPaths()
	{
		// keep a time based cache of the Set
		if (fBuildPaths == null || System.currentTimeMillis() > fTimestamp + CACHE_TIMEOUT)
		{
			LinkedHashSet<IBuildPathEntry> result = new LinkedHashSet<IBuildPathEntry>();
			// Add static paths, if we have any
			if (whitelistedBuildPaths != null)
			{
				result.addAll(whitelistedBuildPaths);
			}

			// Add dynamic paths
			result.addAll(getDynamicBuildPaths());

			// Now cache the result and record the time
			fBuildPaths = result;
			fTimestamp = System.currentTimeMillis();
		}

		return new LinkedHashSet<IBuildPathEntry>(fBuildPaths);
	}

	/**
	 * Returns the set of build paths for a project. This set cannot be relied upon for modifications (i.e. it is likely
	 * to be unmodifiable). Make a copy if you plan to add/remove entries.
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
						catch (Exception e)
						{
							// @formatter:off
							String message = MessageFormat.format(
								Messages.BuildPathManager_UnableToConvertURI,
								uri,
								PROJECT_BUILD_PATH_PROPERTY_NAME,
								project.getName()
							);
							// @formatter:on

							IdeLog.logWarning(BuildPathCorePlugin.getDefault(), message, e);
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

			IdeLog.logWarning(BuildPathCorePlugin.getDefault(), message, e);
		}

		// OK, now grab the build paths contributed dynamically by contributors for this project!
		if (contributors != null)
		{
			for (IBuildPathContributor contributor : contributors)
			{
				List<IBuildPathEntry> entries = contributor.getBuildPathEntries(project);
				if (!CollectionsUtil.isEmpty(entries))
				{
					result.addAll(entries);
				}
			}
		}

		if (!result.isEmpty())
		{
			// only include paths that are actually registered
			result.retainAll(getBuildPaths());
		}

		// Don't allow modifications!
		return Collections.unmodifiableSet(result);
	}

	/**
	 * The set of globally available whitelisted build paths contributed by extensions.
	 * 
	 * @return
	 */
	private Set<IBuildPathEntry> getDynamicBuildPaths()
	{
		if (CollectionsUtil.isEmpty(contributors))
		{
			return Collections.emptySet();
		}

		Set<IBuildPathEntry> result = new LinkedHashSet<IBuildPathEntry>(contributors.size());
		for (IBuildPathContributor contributor : contributors)
		{
			List<IBuildPathEntry> files = contributor.getBuildPathEntries();

			if (files != null)
			{
				result.addAll(files);
			}
		}

		return result;
	}

	/**
	 * hasBuildPath
	 * 
	 * @param entry
	 * @return
	 */
	public boolean hasBuildPath(IBuildPathEntry entry)
	{
		return getBuildPaths().contains(entry);
	}

	/**
	 * Does a given build path entry exist in the set attached to a project?
	 * 
	 * @param project
	 * @param entry
	 * @return
	 */
	public boolean hasBuildPath(IProject project, IBuildPathEntry entry)
	{
		if (project == null || entry == null)
		{
			return false;
		}

		return getBuildPaths(project).contains(entry);
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

				IdeLog.logWarning(BuildPathCorePlugin.getDefault(), message);
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

					IdeLog.logWarning(BuildPathCorePlugin.getDefault(), message);
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

				IdeLog.logWarning(BuildPathCorePlugin.getDefault(), message);
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
	public synchronized boolean removeBuildPath(IBuildPathEntry entry)
	{
		if (entry == null || whitelistedBuildPaths == null)
		{
			return false;
		}

		boolean result = whitelistedBuildPaths.remove(entry);
		if (result)
		{
			fBuildPaths = null;
		}
		return result;
	}

	/**
	 * removeBuildPath
	 * 
	 * @param project
	 * @param entry
	 */
	public boolean removeBuildPath(IProject project, IBuildPathEntry entry)
	{
		if (project == null || entry == null)
		{
			return false;
		}

		// Use a copy!
		Set<IBuildPathEntry> entries = new HashSet<IBuildPathEntry>(getBuildPaths(project));
		if (entries.remove(entry))
		{
			setBuildPaths(project, entries);
			// TODO Remove the index for it? Don't we need to see if no other references to it are out there?
			return true;
		}
		return false;
	}

	/**
	 * setBuildPaths
	 * 
	 * @param project
	 * @param entries
	 */
	public boolean setBuildPaths(IProject project, Collection<IBuildPathEntry> entries)
	{
		if (project == null || entries == null)
		{
			return false;
		}

		List<String> nameAndPaths = CollectionsUtil.map(entries, new IMap<IBuildPathEntry, String>()
		{
			public String map(IBuildPathEntry item)
			{
				return item.getDisplayName() + NAME_AND_PATH_DELIMITER + item.getPath();
			}
		});

		String value = StringUtil.join(BUILD_PATH_ENTRY_DELIMITER, nameAndPaths);

		// FIXME This severely limits the value's size, which we could run into over time! It also does not make the
		// value portable across users/workspaces
		try
		{
			project.setPersistentProperty(getBuildPathPropertyName(), value);
			return true;
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

			IdeLog.logWarning(BuildPathCorePlugin.getDefault(), message, e);
			return false;
		}
	}
}
