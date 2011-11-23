/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.index.core;

import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.jobs.Job;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IConfigurationElementProcessor;
import com.aptana.index.core.build.BuildContext;

abstract class IndexRequestJob extends Job
{
	private static final String INDEX_FILTER_PARTICIPANTS_ID = "indexFilterParticipants"; //$NON-NLS-1$
	private static final String ELEMENT_FILTER = "filter"; //$NON-NLS-1$
	private static final String ATTR_CLASS = "class"; //$NON-NLS-1$

	private static final String FILE_CONTRIBUTORS_ID = "fileContributors"; //$NON-NLS-1$
	private static final String ELEMENT_CONTRIBUTOR = "contributor"; //$NON-NLS-1$

	private URI containerURI;
	private List<IIndexFilterParticipant> filterParticipants;
	private List<IIndexFileContributor> fileContributors;

	/**
	 * IndexRequestJob
	 * 
	 * @param name
	 * @param containerURI
	 */
	protected IndexRequestJob(String name, URI containerURI)
	{
		super(name);
		this.containerURI = containerURI;
		setRule(IndexManager.MUTEX_RULE);
		setPriority(Job.BUILD);
		// setSystem(true);
	}

	/**
	 * IndexRequestJob
	 * 
	 * @param containerURI
	 */
	protected IndexRequestJob(URI containerURI)
	{
		this(MessageFormat.format(Messages.IndexRequestJob_Name, containerURI.toString()), containerURI);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#belongsTo(java.lang.Object)
	 */
	@Override
	public boolean belongsTo(Object family)
	{
		if (getContainerURI() == null)
		{
			return family == null;
		}
		if (family == null)
		{
			return false;
		}
		return getContainerURI().equals(family);
	}

	/**
	 * filterFileStores
	 * 
	 * @return
	 */
	protected Set<IFileStore> filterFileStores(Set<IFileStore> fileStores)
	{
		if (fileStores != null && fileStores.isEmpty() == false)
		{
			for (IIndexFilterParticipant filterParticipant : this.getFilterParticipants())
			{
				fileStores = filterParticipant.applyFilter(fileStores);
			}
		}

		return fileStores;
	}

	/**
	 * getContainerURI
	 * 
	 * @return
	 */
	protected URI getContainerURI()
	{
		return containerURI;
	}

	/**
	 * getContributedFiles
	 * 
	 * @param container
	 * @return
	 */
	protected Set<IFileStore> getContributedFiles(URI container)
	{
		Set<IFileStore> result = new HashSet<IFileStore>();

		for (IIndexFileContributor contributor : this.getFileContributors())
		{
			Set<IFileStore> files = contributor.getFiles(container);

			if (files != null && !files.isEmpty())
			{
				result.addAll(files);
			}
		}

		return result;
	}

	/**
	 * getFileContributors
	 * 
	 * @return
	 */
	private List<IIndexFileContributor> getFileContributors()
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
		}

		return fileContributors;
	}

	/**
	 * getFilterParticipants
	 * 
	 * @return
	 */
	private List<IIndexFilterParticipant> getFilterParticipants()
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
		}

		return filterParticipants;
	}

	/**
	 * getIndex
	 * 
	 * @return
	 */
	protected Index getIndex()
	{
		return IndexManager.getInstance().getIndex(getContainerURI());
	}

	/**
	 * hasTypes
	 * 
	 * @param store
	 * @param types
	 * @return
	 */
	protected boolean hasType(IFileStore store, Set<IContentType> types)
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
			if (type.isAssociatedWith(store.getName()))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Indexes a set of {@link IFileStore}s with the appropriate {@link IFileStoreIndexingParticipant}s that apply to
	 * the content types (matching is done via filename/extension).
	 * 
	 * @param index
	 * @param fileStores
	 * @param monitor
	 * @throws CoreException
	 */
	protected void indexFileStores(Index index, Set<IFileStore> fileStores, IProgressMonitor monitor)
			throws CoreException
	{
		fileStores = this.filterFileStores(fileStores);

		if (index == null || fileStores == null || fileStores.isEmpty())
		{
			return;
		}

		int remaining = fileStores.size();
		SubMonitor sub = SubMonitor.convert(monitor, remaining * 11);
		try
		{
			for (IFileStore file : fileStores)
			{
				if (sub.isCanceled())
				{
					throw new CoreException(Status.CANCEL_STATUS);
				}
				// First cleanup old index entries for file
				index.remove(file.toURI());
				sub.worked(1);

				// Now run indexers on file
				List<IFileStoreIndexingParticipant> indexers = getIndexParticipants(file);
				if (indexers != null && !indexers.isEmpty())
				{
					int work = 10 / indexers.size();
					BuildContext context = new FileStoreBuildContext(file);
					for (IFileStoreIndexingParticipant indexer : indexers)
					{
						if (sub.isCanceled())
						{
							throw new CoreException(Status.CANCEL_STATUS);
						}
						try
						{
							indexer.index(context, index, sub.newChild(work));
						}
						catch (CoreException e)
						{
							IdeLog.logError(IndexPlugin.getDefault(), e);
						}
					}
				}
				// Update remaining units
				remaining--;
				sub.setWorkRemaining(remaining * 11);
			}
		}
		finally
		{
			sub.done();
		}
	}

	protected List<IFileStoreIndexingParticipant> getIndexParticipants(IFileStore file)
	{
		return IndexManager.getInstance().getIndexParticipants(file.getName());
	}

}
