/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.internal.build;

import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.buildpath.core.BuildPathCorePlugin;
import com.aptana.buildpath.core.BuildPathManager;
import com.aptana.buildpath.core.IBuildPathEntry;
import com.aptana.core.CorePlugin;
import com.aptana.core.IDebugScopes;
import com.aptana.core.build.RequiredBuildParticipant;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.index.core.IFileStoreIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexPlugin;
import com.aptana.index.core.build.BuildContext;

public class IndexBuildParticipant extends RequiredBuildParticipant
{

	private Index fIndex;
	private boolean index_trace_enabled = false;
	private boolean advanced_trace_enabled = false;

	public void clean(IProject project, IProgressMonitor monitor)
	{
		IndexManager im = getIndexManager();
		URI uri = getURI(project);
		if (uri != null)
		{
			if (isTraceEnabled())
			{
				logTrace(MessageFormat.format("Cleaning index for project ''{0}'' ({1})", project.getName(), uri)); //$NON-NLS-1$
			}
			im.resetIndex(uri);
		}
		// Reset any additional build paths
		BuildPathManager pathManager = BuildPathManager.getInstance();
		Set<IBuildPathEntry> entries = pathManager.getBuildPaths(project);
		if (!CollectionsUtil.isEmpty(entries))
		{
			for (IBuildPathEntry entry : entries)
			{
				URI path = entry.getPath();
				if (path != null)
				{
					im.resetIndex(path);
				}
			}
		}

		index_trace_enabled = IdeLog.isTraceEnabled(BuildPathCorePlugin.getDefault(), IDebugScopes.BUILDER_INDEXER);
		advanced_trace_enabled = IdeLog.isTraceEnabled(BuildPathCorePlugin.getDefault(), IDebugScopes.BUILDER_ADVANCED);
	}

	public void buildStarting(IProject project, int kind, IProgressMonitor monitor)
	{
		fIndex = getIndex(project);
	}

	public void buildEnding(IProgressMonitor monitor)
	{
		if (fIndex != null)
		{
			try
			{
				fIndex.save();
			}
			catch (IOException e)
			{
				IdeLog.logError(BuildPathCorePlugin.getDefault(), e);
			}
			fIndex = null;
		}
	}

	public void buildFile(BuildContext context, IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);
		try
		{
			// try to grab the associated index. If it's not there, we're probably reconciling on an external file, so
			// just skip indexing
			if (fIndex == null)
			{
				fIndex = getIndex(context.getProject());
				if (fIndex == null)
				{
					return;
				}
			}

			// Check for cancellation
			if (sub.isCanceled())
			{
				return;
			}

			// wipe the index for the file first
			deleteFile(context, sub.newChild(10));

			List<IFileStoreIndexingParticipant> indexers = getIndexParticipants(context);
			if (!CollectionsUtil.isEmpty(indexers))
			{
				int workPerIndexer = 90 / indexers.size();
				for (IFileStoreIndexingParticipant indexer : indexers)
				{
					try
					{
						long startTime = 0;
						if (index_trace_enabled)
						{
							startTime = System.nanoTime();
						}
						indexer.index(context, fIndex, sub.newChild(workPerIndexer));
						if (index_trace_enabled)
						{
							double endTime = ((double) System.nanoTime() - startTime) / 1000000;
							IdeLog.logTrace(
									BuildPathCorePlugin.getDefault(),
									MessageFormat
											.format("Indexed file ''{0}'' via ''{1}'' in {2} ms.", context.getURI(), indexer.getClass().getName(), endTime), IDebugScopes.BUILDER_INDEXER); //$NON-NLS-1$
						}
					}
					catch (CoreException e)
					{
						IdeLog.logError(BuildPathCorePlugin.getDefault(), MessageFormat.format(
								"Failed to index file {0} with indexer {1}", context.getURI(), indexer.getClass() //$NON-NLS-1$
										.getName()), e);
					}

					// stop indexing if it has been canceled
					if (sub.isCanceled())
					{
						break;
					}
				}
			}
			else
			{
				if (advanced_trace_enabled)
				{
					IdeLog.logTrace(
							BuildPathCorePlugin.getDefault(),
							MessageFormat.format("No indexers available for file ''{0}''", context.getURI()), IDebugScopes.BUILDER_ADVANCED); //$NON-NLS-1$
				}
			}
		}
		finally
		{
			sub.done();
		}
	}

	public void deleteFile(BuildContext context, IProgressMonitor monitor)
	{
		if (fIndex == null)
		{
			fIndex = getIndex(context.getProject());
			if (fIndex == null)
			{
				return;
			}
		}
		fIndex.remove(context.getURI());
		if (advanced_trace_enabled)
		{
			IdeLog.logTrace(
					BuildPathCorePlugin.getDefault(),
					MessageFormat.format("Wiped index for file ''{0}''", context.getURI()), IDebugScopes.BUILDER_ADVANCED); //$NON-NLS-1$
		}
	}

	protected URI getURI(IProject project)
	{
		if (project == null)
		{
			return null;
		}
		URI uri = project.getLocationURI();
		if (uri != null)
		{
			return uri;
		}
		IdeLog.logError(BuildPathCorePlugin.getDefault(),
				MessageFormat.format("Project's location URI is null. raw location: {0}, path: {1}", //$NON-NLS-1$
						project.getRawLocationURI(), project.getFullPath()));
		return project.getRawLocationURI();
	}

	protected Index getIndex(IProject project)
	{
		if (project == null)
		{
			return null;
		}
		return getIndexManager().getIndex(getURI(project));
	}

	protected List<IFileStoreIndexingParticipant> getIndexParticipants(BuildContext context)
	{
		return getIndexManager().getIndexParticipants(context.getName());
	}

	protected IndexManager getIndexManager()
	{
		return IndexPlugin.getDefault().getIndexManager();
	}

	protected static void logTrace(String message)
	{
		IdeLog.logInfo(BuildPathCorePlugin.getDefault(), message, IDebugScopes.BUILDER);
	}

	protected static boolean isTraceEnabled()
	{
		return IdeLog.isTraceEnabled(CorePlugin.getDefault(), IDebugScopes.BUILDER);
	}

}
