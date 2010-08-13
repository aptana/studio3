package com.aptana.index.core.ui;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.jface.preference.IPreferenceStore;

import com.aptana.core.util.StringUtil;
import com.aptana.index.core.ui.preferences.IPreferenceConstants;

public class IndexFilterManager
{
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

	private Set<URI> _filteredURIs;

	/**
	 * IndexFilterManager
	 */
	private IndexFilterManager()
	{
		this.loadFilteredURIs();
	}

	/**
	 * addFilteredURI
	 * 
	 * @param uri
	 */
	public void addFilteredURI(URI uri)
	{
		if (uri != null)
		{
			if (this._filteredURIs == null)
			{
				this._filteredURIs = new HashSet<URI>();
			}

			this._filteredURIs.add(uri);
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
		if (this._filteredURIs == null && this._filteredURIs.isEmpty() == false && fileStores != null)
		{
			Set<IFileStore> toRemove = new HashSet<IFileStore>();
			
			for (URI uri : this._filteredURIs)
			{
				String uriString = uri.normalize().toString();
				
				for (IFileStore fileStore : fileStores)
				{
					String fileStoreUriString = fileStore.toURI().normalize().toString();
					
					if (fileStoreUriString.startsWith(uriString))
					{
						toRemove.add(fileStore);
					}
				}
			}
			
			fileStores.removeAll(toRemove);
		}
		
		return fileStores;
	}

	/**
	 * getFilteredURIs
	 * 
	 * @return
	 */
	public Set<URI> getFilteredURIs()
	{
		Set<URI> result = this._filteredURIs;

		if (result == null)
		{
			result = Collections.emptySet();
		}

		return result;
	}

	/**
	 * loadFilteredURIs
	 */
	public void loadFilteredURIs()
	{
		this._filteredURIs = new HashSet<URI>();

		IPreferenceStore prefs = IndexUiActivator.getDefault().getPreferenceStore();
		String uris = prefs.getString(IPreferenceConstants.FILTERED_INDEX_URIS);

		if (uris != null && uris.length() != 0)
		{
			for (String uri : uris.split(","))
			{
				try
				{
					this._filteredURIs.add(new URI(uri));
				}
				catch (URISyntaxException e)
				{
				}
			}
		}

	}

	/**
	 * saveFilteredURIs
	 */
	public void saveFilteredURIs()
	{
		String value;

		if (this._filteredURIs != null)
		{
			List<String> uris = new ArrayList<String>();

			for (URI uri : this._filteredURIs)
			{
				uris.add(uri.toString());
			}

			value = StringUtil.join(",", uris);
		}
		else
		{
			value = "";
		}

		IPreferenceStore prefs = IndexUiActivator.getDefault().getPreferenceStore();

		prefs.putValue(IPreferenceConstants.FILTERED_INDEX_URIS, value);
	}
}
