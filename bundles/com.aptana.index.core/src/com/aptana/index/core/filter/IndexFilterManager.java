/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.index.core.filter;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.IFilter;
import com.aptana.core.IMap;
import com.aptana.core.io.efs.EFSUtils;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.index.core.IPreferenceConstants;
import com.aptana.index.core.IndexPlugin;

public class IndexFilterManager
{
	private static final String ITEM_DELIMITER = "\0"; //$NON-NLS-1$
	/**
	 * Unfortunately, we stored filters in the ui plugin prefs...
	 */
	private static IndexFilterManager INSTANCE;

	/**
	 * getInstance
	 * 
	 * @return
	 */
	public synchronized static IndexFilterManager getInstance()
	{
		// TODO Hide the singleton off the plugin and have it manage it's lifecycle!
		if (INSTANCE == null)
		{
			INSTANCE = new IndexFilterManager();
		}

		return INSTANCE;
	}

	private Set<IFileStore> _filteredItems;

	/**
	 * IndexFilterManager
	 */
	private IndexFilterManager()
	{
		this._filteredItems = loadFilteredItems();
	}

	/**
	 * addFilterItem
	 * 
	 * @param IFileStore
	 */
	public void addFilterItem(IFileStore item)
	{
		if (item == null)
		{
			return;
		}

		// only add if this item isn't being filtered already
		if (!isFilteredItem(item))
		{
			// remove any pre-existing file stores that are children of the
			// item we're about to add
			Set<IFileStore> toRemove = new HashSet<IFileStore>(this._filteredItems.size());

			for (IFileStore candidate : this._filteredItems)
			{
				if (item.isParentOf(candidate))
				{
					toRemove.add(candidate);
				}
			}

			this._filteredItems.removeAll(toRemove);

			// add new item to our list
			this._filteredItems.add(item);
		}
	}

	/**
	 * commitFilteredItems
	 */
	public void commitFilteredItems()
	{
		// build a preference value of all file stores we are filtering
		List<String> uris = CollectionsUtil.map(_filteredItems, new IMap<IFileStore, String>()
		{
			public String map(IFileStore item)
			{
				URI uri = item.toURI();
				return uri.toString();
			}
		});
		String value = StringUtil.join(ITEM_DELIMITER, uris);

		// now save the file store list
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(IndexPlugin.PLUGIN_ID);
		prefs.put(IPreferenceConstants.FILTERED_INDEX_URIS, value);
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e1)
		{
			// ignore
		}

		// determine which file stores were added and deleted
		Collection<IFileStore> nonOverlapping = CollectionsUtil.getNonOverlapping(loadFilteredItems(), _filteredItems);

		// determine which projects have been affected by the last set of changes
		Set<IProject> projects = new HashSet<IProject>(nonOverlapping.size());
		for (IFileStore f : nonOverlapping)
		{
			IResource resource = (IResource) f.getAdapter(IResource.class);

			if (resource != null)
			{
				projects.add(resource.getProject());
			}
		}

		// update project indexes that were affected by our changes
		for (final IProject p : projects)
		{
			Job job = new Job(MessageFormat.format(Messages.IndexFilterManager_Rebuilding_0, p.getName()))
			{
				protected IStatus run(IProgressMonitor monitor)
				{
					try
					{
						p.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
					}
					catch (CoreException e)
					{
						return e.getStatus();
					}
					return Status.OK_STATUS;
				}

			};
			job.schedule();
		}
	}

	/**
	 * applyFilter This will apply the filter in place to the passed in Set, so beware!
	 * 
	 * @param fileStores
	 * @return
	 */
	protected Set<IFileStore> applyFilter(Set<IFileStore> fileStores)
	{
		if (CollectionsUtil.isEmpty(_filteredItems) || fileStores == null)
		{
			return fileStores;
		}

		final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		CollectionsUtil.filterInPlace(fileStores, new IFilter<IFileStore>()
		{

			public boolean include(IFileStore item)
			{
				// NOTE: The indexing system creates LocalFiles but filters are based
				// on WorkspaceFiles. The following tries to convert each LocalFile
				// in the set to a WorkspaceFile before testing if the item needs to
				// be filtered.
				for (IContainer container : workspaceRoot.findContainersForLocationURI(item.toURI()))
				{
					IFileStore workspaceFileStore = EFSUtils.getFileStore(container);

					if (isFilteredItem(workspaceFileStore))
					{
						return false;
					}
				}
				return true;
			}
		});

		return fileStores;
	}

	/**
	 * getFilteredItems
	 * 
	 * @return
	 */
	public Set<IFileStore> getFilteredItems()
	{
		return CollectionsUtil.getSetValue(_filteredItems);
	}

	/**
	 * isFilteredItem
	 * 
	 * @param item
	 * @return
	 */
	public boolean isFilteredItem(IFileStore item)
	{
		if (item != null)
		{
			for (IFileStore candidate : getFilteredItems())
			{
				if (candidate.equals(item) || candidate.isParentOf(item))
				{
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * loadFilteredItems
	 */
	private Set<IFileStore> loadFilteredItems()
	{
		String uris = Platform.getPreferencesService().getString(IndexPlugin.PLUGIN_ID,
				IPreferenceConstants.FILTERED_INDEX_URIS, null, null);
		if (StringUtil.isEmpty(uris))
		{
			// Don't return emptySet because we expect to be able to modify the return value
			return new HashSet<IFileStore>(0);
		}

		String[] urisSplit = uris.split(ITEM_DELIMITER);
		Set<IFileStore> filteredItems = new HashSet<IFileStore>(urisSplit.length);
		for (String uriString : urisSplit)
		{
			try
			{
				URI uri = new URI(uriString);
				IFileStore item = EFS.getStore(uri);

				// TODO: Is it possible to have non-local files in projects
				// and will this still work for those?
				if (item.fetchInfo().exists())
				{
					filteredItems.add(item);
				}
			}
			catch (URISyntaxException e)
			{
				IdeLog.logError(IndexPlugin.getDefault(), e.getMessage(), e);
			}
			catch (CoreException e)
			{
				IdeLog.logError(IndexPlugin.getDefault(), e);
			}
		}

		return filteredItems;
	}

	/**
	 * removeFilterItem
	 * 
	 * @param item
	 */
	public void removeFilterItem(IFileStore item)
	{
		if (item != null && this._filteredItems != null)
		{
			this._filteredItems.remove(item);
		}
	}
}
