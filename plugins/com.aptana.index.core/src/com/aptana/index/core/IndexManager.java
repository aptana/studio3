/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.index.core;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IConfigurationElementProcessor;
import com.aptana.core.util.StringUtil;
import com.aptana.index.core.filter.IIndexFilterParticipant;

public class IndexManager
{
	/**
	 * Constants for dealing with file indexers through the extension point.
	 */
	private static final String CONTENT_TYPE_ID = "contentTypeId"; //$NON-NLS-1$
	private static final String CONTENT_TYPE_BINDING = "contentTypeBinding"; //$NON-NLS-1$
	private static final String FILE_INDEXING_PARTICIPANTS_ID = "fileIndexingParticipants"; //$NON-NLS-1$
	private static final String TAG_FILE_INDEXING_PARTICIPANT = "fileIndexingParticipant"; //$NON-NLS-1$
	private static final String ATTR_CLASS = "class"; //$NON-NLS-1$
	private static final String INDEX_FILTER_PARTICIPANTS_ID = "indexFilterParticipants"; //$NON-NLS-1$
	private static final String ELEMENT_FILTER = "filter"; //$NON-NLS-1$
	private static final String FILE_CONTRIBUTORS_ID = "fileContributors"; //$NON-NLS-1$
	private static final String ELEMENT_CONTRIBUTOR = "contributor"; //$NON-NLS-1$

	private Map<URI, Index> indexes;

	private ArrayList<IIndexFileContributor> fileContributors;
	private ArrayList<IIndexFilterParticipant> filterParticipants;

	static final ISchedulingRule MUTEX_RULE = new ISchedulingRule()
	{
		public boolean contains(ISchedulingRule rule)
		{
			return rule == this;
		}

		public boolean isConflicting(ISchedulingRule rule)
		{
			return rule == this;
		}
	};

	/**
	 * IndexManager
	 */
	IndexManager()
	{
		this.indexes = new HashMap<URI, Index>();
	}

	/**
	 * getIndex
	 * 
	 * @param path
	 * @return
	 */
	public synchronized Index getIndex(URI path)
	{
		Index index = this.indexes.get(path);

		if (index == null)
		{
			try
			{
				// First try to re-use an existing file if possible
				index = new Index(path, true);
				indexes.put(path, index);
			}
			catch (IOException e)
			{
				try
				{
					// We failed. Most likely disk index signature changed or got corrupted.
					// Don't re-use the file (create an empty index file)
					index = new Index(path, false);
					this.indexes.put(path, index);

					// force a rebuild of the index.
					new RebuildIndexJob(path).schedule();
				}
				catch (IOException e1)
				{
					IdeLog.logError(IndexPlugin.getDefault(), "An error occurred while trying to access an index", e1); //$NON-NLS-1$
				}
			}
		}

		return index;
	}

	/**
	 * getIndexPaths
	 * 
	 * @return
	 */
	public synchronized List<URI> getIndexPaths()
	{
		return new ArrayList<URI>(indexes.keySet());
	}

	/**
	 * Removes the index for a given path. This is a no-op if the index did not exist. DO NOT USE THIS IF YOU'RE
	 * RE-INDEXING THE CONTENTS! IT WILL BREAK ANY CURRENT REFS TO THIS INDEX. USE {@link #resetIndex(URI)}! This method
	 * is used to remove indices entirely when we do not plan re-use them (i.e. the project it represented was deleted)
	 */
	public synchronized void removeIndex(URI path)
	{
		Index index = getIndex(path);

		if (index != null)
		{
			index.deleteIndexFile();
		}

		this.indexes.remove(path);
	}

	/**
	 * Return a map from classname of the participant to a set of strings for the content type ids it applies to.
	 * 
	 * @return
	 */
	private Map<IConfigurationElement, Set<IContentType>> getFileIndexingParticipants()
	{
		final Map<IConfigurationElement, Set<IContentType>> map = new HashMap<IConfigurationElement, Set<IContentType>>();
		final IContentTypeManager manager = Platform.getContentTypeManager();

		EclipseUtil.processConfigurationElements(IndexPlugin.PLUGIN_ID, FILE_INDEXING_PARTICIPANTS_ID,
				new IConfigurationElementProcessor()
				{

					public void processElement(IConfigurationElement element)
					{
						Set<IContentType> types = new HashSet<IContentType>();

						IConfigurationElement[] contentTypes = element.getChildren(CONTENT_TYPE_BINDING);
						for (IConfigurationElement contentTypeBinding : contentTypes)
						{
							String contentTypeId = contentTypeBinding.getAttribute(CONTENT_TYPE_ID);
							IContentType type = manager.getContentType(contentTypeId);
							types.add(type);
						}
						map.put(element, types);
					}

					public Set<String> getSupportElementNames()
					{
						return CollectionsUtil.newSet(TAG_FILE_INDEXING_PARTICIPANT);
					}
				});

		return map;
	}

	/**
	 * Returns an ordered list of the file indexing participants registered for the given filename's associated content
	 * types.
	 * 
	 * @param filename
	 * @return
	 */
	public List<IFileStoreIndexingParticipant> getIndexParticipants(String filename)
	{
		Map<IConfigurationElement, Set<IContentType>> participantstoContentTypes = getFileIndexingParticipants();
		Set<IFileStoreIndexingParticipant> participants = new HashSet<IFileStoreIndexingParticipant>(
				participantstoContentTypes.size());
		for (Map.Entry<IConfigurationElement, Set<IContentType>> entry : participantstoContentTypes.entrySet())
		{
			if (hasType(filename, entry.getValue()))
			{
				IFileStoreIndexingParticipant participant = createParticipant(entry.getKey());
				if (participant != null)
				{
					participants.add(participant);
				}
			}
		}

		List<IFileStoreIndexingParticipant> result = new ArrayList<IFileStoreIndexingParticipant>(participants);
		Collections.sort(result, new Comparator<IFileStoreIndexingParticipant>()
		{
			public int compare(IFileStoreIndexingParticipant arg0, IFileStoreIndexingParticipant arg1)
			{
				// sort higher first
				return arg1.getPriority() - arg0.getPriority();
			}
		});

		return result;
	}

	/**
	 * Determines if a given content type is associated with the filename.
	 * 
	 * @param filename
	 * @param types
	 * @return
	 */
	private boolean hasType(String filename, Set<IContentType> types)
	{
		if (types == null || types.isEmpty())
		{
			return false;
		}
		for (IContentType type : types)
		{
			if (type == null)
			{
				continue;
			}
			if (type.isAssociatedWith(filename))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Instantiate a {@link IFileStoreIndexingParticipant} from the {@link IConfigurationElement} pointing to it via an
	 * extension.
	 * 
	 * @param key
	 * @return
	 */
	private IFileStoreIndexingParticipant createParticipant(IConfigurationElement key)
	{
		try
		{
			return (IFileStoreIndexingParticipant) key.createExecutableExtension(ATTR_CLASS);
		}
		catch (CoreException e)
		{
			IdeLog.logError(IndexPlugin.getDefault(), e);
		}

		return null;
	}

	/**
	 * getFileContributors
	 * 
	 * @return
	 */
	public synchronized List<IIndexFileContributor> getFileContributors()
	{
		if (fileContributors == null)
		{
			fileContributors = new ArrayList<IIndexFileContributor>();
			EclipseUtil.processConfigurationElements(IndexPlugin.PLUGIN_ID, FILE_CONTRIBUTORS_ID,
					new IConfigurationElementProcessor()
					{

						public void processElement(IConfigurationElement element)
						{
							try
							{
								IIndexFileContributor participant = (IIndexFileContributor) element
										.createExecutableExtension(ATTR_CLASS);
								fileContributors.add(participant);
							}
							catch (CoreException e)
							{
								IdeLog.logError(IndexPlugin.getDefault(), e);
							}
						}

						public Set<String> getSupportElementNames()
						{
							return CollectionsUtil.newSet(ELEMENT_CONTRIBUTOR);
						}
					});
			fileContributors.trimToSize();
		}

		return fileContributors;
	}

	/**
	 * getFilterParticipants
	 * 
	 * @return
	 */
	public synchronized List<IIndexFilterParticipant> getFilterParticipants()
	{
		if (filterParticipants == null)
		{
			filterParticipants = new ArrayList<IIndexFilterParticipant>();
			EclipseUtil.processConfigurationElements(IndexPlugin.PLUGIN_ID, INDEX_FILTER_PARTICIPANTS_ID,
					new IConfigurationElementProcessor()
					{

						public void processElement(IConfigurationElement element)
						{
							try
							{
								IIndexFilterParticipant participant = (IIndexFilterParticipant) element
										.createExecutableExtension(ATTR_CLASS);
								filterParticipants.add(participant);
							}
							catch (CoreException e)
							{
								IdeLog.logError(IndexPlugin.getDefault(), e);
							}
						}

						public Set<String> getSupportElementNames()
						{
							return CollectionsUtil.newSet(ELEMENT_FILTER);
						}
					});
			filterParticipants.trimToSize();
		}

		return filterParticipants;
	}

	/**
	 * Resets the index for a given path. Returns true if the index was reset, false otherwise.
	 */
	public synchronized boolean resetIndex(URI path)
	{

		try
		{
			Index index = getIndex(path);

			if (index != null)
			{
				index.reset();
				return true;
			}

			return recreateIndex(path) != null;
		}
		catch (IOException e)
		{
			// The file could not be created. Possible reason: the project has been deleted.
			IdeLog.logError(IndexPlugin.getDefault(), e);
			return false;
		}
	}

	/**
	 * Recreates the index for a given path, keeping the same read-write monitor. Returns the new empty index or null if
	 * it didn't exist before. Warning: Does not check whether index is consistent (not being used)
	 */
	private synchronized Index recreateIndex(URI path)
	{
		try
		{
			// Path is already canonical
			Index index = getIndex(path);

			ReadWriteLock monitor = index == null ? null : index.monitor;

			index = new Index(path, false);
			indexes.put(path, index);
			index.monitor = monitor;
			return index;
		}
		catch (IOException e)
		{
			// The file could not be created. Possible reason: the project has been deleted.
			IdeLog.logError(IndexPlugin.getDefault(), e);
			return null;
		}
	}

	public String toString()
	{
		return "[" + StringUtil.join(", ", indexes.values()) + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}
