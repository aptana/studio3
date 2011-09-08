/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.index.core.build;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.core.CorePlugin;
import com.aptana.core.IDebugScopes;
import com.aptana.core.build.IBuildParticipant;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.index.core.IndexFilesOfProjectJob;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexPlugin;
import com.aptana.index.core.RebuildIndexJob;
import com.aptana.index.core.RemoveIndexOfFilesOfProjectJob;

public class IndexBuildParticipant implements IBuildParticipant
{

	public void clean(URI uri, IProgressMonitor monitor)
	{
		IndexManager.getInstance().removeIndex(uri);
	}

	public void fullBuild(URI uri, IProgressMonitor monitor)
	{
		// FIXME Run rebuild index, or IndexProject?
		RebuildIndexJob job = new RebuildIndexJob(uri);
		job.schedule();
	}

	public void incrementalBuild(IResourceDelta delta, IProject project, IProgressMonitor monitor)
	{
		if (delta != null)
		{
			ResourceCollector resourceCollector = new ResourceCollector();
			try
			{
				delta.accept(resourceCollector);

				// TODO Pre-filter by removing any files from "to be indexed" that don't have an indexer?

				if (IdeLog.isInfoEnabled(CorePlugin.getDefault(), IDebugScopes.BUILDER))
				{
					IFile[] toRemove = resourceCollector.filesToRemoveFromIndex
							.toArray(new IFile[resourceCollector.filesToRemoveFromIndex.size()]);
					IFile[] toIndex = resourceCollector.filesToIndex.toArray(new IFile[resourceCollector.filesToIndex
							.size()]);
					IdeLog.logInfo(
							IndexPlugin.getDefault(),
							StringUtil.format(Messages.IndexBuildParticipant_IndexingResourceDelta, new Object[] {
									Arrays.deepToString(toRemove), Arrays.deepToString(toIndex) }),
							IDebugScopes.BUILDER);
				}

				if (!resourceCollector.filesToRemoveFromIndex.isEmpty())
				{
					RemoveIndexOfFilesOfProjectJob removeJob = new RemoveIndexOfFilesOfProjectJob(project,
							resourceCollector.filesToRemoveFromIndex);
					removeJob.schedule();
				}

				if (!resourceCollector.filesToIndex.isEmpty())
				{
					IndexFilesOfProjectJob indexJob = new IndexFilesOfProjectJob(project,
							resourceCollector.filesToIndex);
					indexJob.schedule();
				}
			}
			catch (CoreException e)
			{
				IdeLog.logError(IndexPlugin.getDefault(), e);
			}
		}
	}

	private static class ResourceCollector implements IResourceDeltaVisitor
	{
		Set<IFile> filesToIndex = new HashSet<IFile>();
		Set<IFile> filesToRemoveFromIndex = new HashSet<IFile>();

		public boolean visit(IResourceDelta delta) throws CoreException
		{
			IResource resource = delta.getResource();
			if (resource instanceof IFile)
			{
				if (delta.getKind() == IResourceDelta.ADDED
						|| (delta.getKind() == IResourceDelta.CHANGED && ((delta.getFlags() & (IResourceDelta.CONTENT | IResourceDelta.ENCODING)) != 0)))
				{
					filesToIndex.add((IFile) resource);
				}
				else if (delta.getKind() == IResourceDelta.REMOVED)
				{
					filesToRemoveFromIndex.add((IFile) resource);
				}
			}
			return true;
		}
	}
}
