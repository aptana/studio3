package com.aptana.index.core.ui;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;

import com.aptana.core.util.StringUtil;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexProjectJob;
import com.aptana.index.core.ui.preferences.IPreferenceConstants;

public class IndexFilterManager
{
	private static final String NO_ITEMS = "";
	private static final String ITEM_DELIMITER = ",";
	private static IndexFilterManager INSTANCE;

	/**
	 * getInstance
	 * 
	 * @return
	 */
	public static IndexFilterManager getInstance()
	{
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
		this._filteredItems = this.loadFilteredItems();
	}

	/**
	 * addFilterItem
	 * 
	 * @param IFileStore
	 */
	public void addFilterItem(IFileStore item)
	{
		if (item != null)
		{
			if (this._filteredItems == null)
			{
				this._filteredItems = new HashSet<IFileStore>();
			}

			if (this.isFilteredItem(item) == false)
			{
				this._filteredItems.add(item);
			}
		}
	}

	/**
	 * commitFilteredItems
	 */
	public void commitFilteredItems()
	{
		Set<IFileStore> deletedItems = this.loadFilteredItems();
		Set<IFileStore> addedItems = new HashSet<IFileStore>(this._filteredItems);

		addedItems.removeAll(deletedItems);
		deletedItems.removeAll(this._filteredItems);

		// now save our new list
		String value;

		if (this._filteredItems != null)
		{
			List<String> uris = new ArrayList<String>();

			for (IFileStore item : this._filteredItems)
			{
				URI uri = item.toURI();

				uris.add(uri.toString());
			}

			value = StringUtil.join(ITEM_DELIMITER, uris);
		}
		else
		{
			value = NO_ITEMS;
		}

		IPreferenceStore prefs = IndexUiActivator.getDefault().getPreferenceStore();

		prefs.putValue(IPreferenceConstants.FILTERED_INDEX_URIS, value);

		// determine which projects have been affected by the last set of changes
		Set<IProject> projects = new HashSet<IProject>();
		
		for (IFileStore f : addedItems)
		{
			IResource resource = (IResource) f.getAdapter(IResource.class);
			
			if (resource != null)
			{
				projects.add(resource.getProject());
			}
		}
		for (IFileStore f : deletedItems)
		{
			IResource resource = (IResource) f.getAdapter(IResource.class);
			
			if (resource != null)
			{
				projects.add(resource.getProject());
			}
		}
		
		// update project indexes that were affected by our settings
		IndexManager manager = IndexManager.getInstance();
		
		for (IProject p : projects)
		{
			// remove project index
			manager.removeIndex(p.getLocationURI());
			
			// and then re-build it
			new IndexProjectJob(p).schedule();
		}
	}

	/**
	 * filterFileStores
	 * 
	 * @param fileStores
	 * @return
	 */
	public Set<IFileStore> filterFileStores(Set<IFileStore> fileStores)
	{
		if (this._filteredItems == null && this._filteredItems.isEmpty() == false && fileStores != null)
		{
			Set<IFileStore> toRemove = new HashSet<IFileStore>();

			for (IFileStore fileStore : fileStores)
			{
				if (this.isFilteredItem(fileStore))
				{
					toRemove.add(fileStore);
				}
			}

			fileStores.removeAll(toRemove);
		}

		return fileStores;
	}

	/**
	 * getFilteredItems
	 * 
	 * @return
	 */
	public Set<IFileStore> getFilteredItems()
	{
		Set<IFileStore> result = this._filteredItems;

		if (result == null)
		{
			result = Collections.emptySet();
		}

		return result;
	}

	/**
	 * isFilteredItem
	 * 
	 * @param item
	 * @return
	 */
	public boolean isFilteredItem(IFileStore item)
	{
		boolean result = false;
		
		if (item != null)
		{
			for (IFileStore candidate : this._filteredItems)
			{
				if (candidate.equals(item) || candidate.isParentOf(item))
				{
					result = true;
					break;
				}
			}
		}
		
		return result;
	}
	
	/**
	 * loadFilteredItems
	 */
	public Set<IFileStore> loadFilteredItems()
	{
		Set<IFileStore> filteredItems = new HashSet<IFileStore>();

		IPreferenceStore prefs = IndexUiActivator.getDefault().getPreferenceStore();
		String uris = prefs.getString(IPreferenceConstants.FILTERED_INDEX_URIS);

		if (uris != null && uris.length() != 0)
		{
			for (String uriString : uris.split(ITEM_DELIMITER))
			{
				try
				{
					URI uri = new URI(uriString);
					IFileStore item = EFS.getStore(uri);

					filteredItems.add(item);
				}
				catch (URISyntaxException e)
				{
					IndexUiActivator.logError(e.getMessage(), e);
				}
				catch (CoreException e)
				{
					IndexUiActivator.logError(e);
				}
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
