/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.index.core.build;

import java.net.URI;
import java.text.MessageFormat;
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
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.core.CorePlugin;
import com.aptana.core.IDebugScopes;
import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.index.core.IndexFilesOfProjectJob;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexPlugin;
import com.aptana.index.core.RebuildIndexJob;
import com.aptana.index.core.RemoveIndexOfFilesOfProjectJob;

public class IndexBuildParticipant extends AbstractBuildParticipant
{

	public void clean(IProject project, IProgressMonitor monitor)
	{
		URI uri = getURI(project);
		if (uri != null)
		{
			if (IdeLog.isInfoEnabled(CorePlugin.getDefault(), IDebugScopes.BUILDER))
			{
				String message = MessageFormat.format("Cleaning index for project {0} ({1})", project.getName(), uri); //$NON-NLS-1$
				IdeLog.logInfo(IndexPlugin.getDefault(), message, IDebugScopes.BUILDER);
			}
			IndexManager.getInstance().removeIndex(uri);
		}
	}

	public void fullBuild(IProject project, IProgressMonitor monitor)
	{
		// FIXME Run rebuild index, or IndexProject?
		URI uri = getURI(project);
		if (uri != null)
		{
			RebuildIndexJob job = new RebuildIndexJob(uri);
			// run sync and report the progress in the provided monitor
			job.run(monitor);
		}
	}

	public void incrementalBuild(IResourceDelta delta, IProject project, IProgressMonitor monitor)
	{
		if (delta != null)
		{
			SubMonitor sub = SubMonitor.convert(monitor, 3);
			ResourceCollector resourceCollector = new ResourceCollector();
			try
			{
				delta.accept(resourceCollector);
				sub.worked(1);

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
					removeJob.run(sub.newChild(1));
				}

				if (!resourceCollector.filesToIndex.isEmpty())
				{
					IndexFilesOfProjectJob indexJob = new IndexFilesOfProjectJob(project,
							resourceCollector.filesToIndex);
					indexJob.run(sub.newChild(1));
				}
			}
			catch (CoreException e)
			{
				IdeLog.logError(IndexPlugin.getDefault(), e);
			}
			sub.done();
		}
	}

	private static URI getURI(IProject project)
	{
		URI uri = project.getLocationURI();
		if (uri != null)
		{
			return uri;
		}
		IdeLog.logError(IndexPlugin.getDefault(),
				MessageFormat.format("Project's location URI is null. raw location: {0}, path: {1}", //$NON-NLS-1$
						project.getRawLocationURI(), project.getFullPath()));
		return project.getRawLocationURI();
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
