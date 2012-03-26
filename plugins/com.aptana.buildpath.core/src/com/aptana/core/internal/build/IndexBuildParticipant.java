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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.buildpath.core.BuildPathCorePlugin;
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

	public void clean(IProject project, IProgressMonitor monitor)
	{
		URI uri = getURI(project);
		if (uri != null)
		{
			if (isTraceEnabled())
			{
				logTrace(MessageFormat.format("Cleaning index for project {0} ({1})", project.getName(), uri)); //$NON-NLS-1$
			}
			getIndexManager().removeIndex(uri);
		}
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
		deleteFile(context, sub.newChild(10));

		List<IFileStoreIndexingParticipant> indexers = getIndexParticipants(context);
		if (!CollectionsUtil.isEmpty(indexers))
		{
			int unit = 90 / indexers.size();
			for (IFileStoreIndexingParticipant indexer : indexers)
			{
				try
				{
					indexer.index(context, fIndex, sub.newChild(unit));
				}
				catch (CoreException e)
				{
					IdeLog.logError(BuildPathCorePlugin.getDefault(), e);
				}

				// stop indexing if it has been canceled
				if (sub.isCanceled())
				{
					break;
				}
			}
		}
		sub.done();
	}

	public void deleteFile(BuildContext context, IProgressMonitor monitor)
	{
		if (fIndex == null)
		{
			fIndex = getIndex(context.getProject());
		}
		fIndex.remove(context.getURI());
	}

	protected URI getURI(IProject project)
	{
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
