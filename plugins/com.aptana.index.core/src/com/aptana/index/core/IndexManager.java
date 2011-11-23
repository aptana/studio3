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
	private static final String ATTR_PRIORITY = "priority"; //$NON-NLS-1$
	private static final int DEFAULT_PRIORITY = 50;

	private static IndexManager INSTANCE;
	private Map<URI, Index> indexes;

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
	 * getInstance
	 * 
	 * @return
	 */
	public synchronized static IndexManager getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new IndexManager();
		}

		return INSTANCE;
	}

	/**
	 * IndexManager
	 */
	private IndexManager()
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
	 * Removes the index for a given path. This is a no-op if the index did not exist.
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

					@Override
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
		Set<IFileStoreIndexingParticipant> participants = new HashSet<IFileStoreIndexingParticipant>();
		Map<IConfigurationElement, Set<IContentType>> participantstoContentTypes = getFileIndexingParticipants();
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
			String priorityString = key.getAttribute(ATTR_PRIORITY);
			int priority = DEFAULT_PRIORITY;

			try
			{
				priority = Integer.parseInt(priorityString);
			}
			catch (NumberFormatException e)
			{
				IdeLog.logError(IndexPlugin.getDefault(), e);
			}

			IFileStoreIndexingParticipant result = (IFileStoreIndexingParticipant) key
					.createExecutableExtension(ATTR_CLASS);

			result.setPriority(priority);

			return result;
		}
		catch (CoreException e)
		{
			IdeLog.logError(IndexPlugin.getDefault(), e);
		}

		return null;
	}
}
