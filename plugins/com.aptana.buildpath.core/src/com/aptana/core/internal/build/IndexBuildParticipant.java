/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.internal.build;

import java.net.URI;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.buildpath.core.BuildPathCorePlugin;
import com.aptana.core.CorePlugin;
import com.aptana.core.IDebugScopes;
import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.logging.IdeLog;
import com.aptana.index.core.IFileStoreIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.build.BuildContext;

public class IndexBuildParticipant extends AbstractBuildParticipant
{

	private Index fIndex;

	public void clean(IProject project, IProgressMonitor monitor)
	{
		URI uri = getURI(project);
		if (uri != null)
		{
			if (IdeLog.isInfoEnabled(CorePlugin.getDefault(), IDebugScopes.BUILDER))
			{
				String message = MessageFormat.format("Cleaning index for project {0} ({1})", project.getName(), uri); //$NON-NLS-1$
				IdeLog.logInfo(BuildPathCorePlugin.getDefault(), message, IDebugScopes.BUILDER);
			}
			IndexManager.getInstance().removeIndex(uri);
		}
	}

	public void buildStarting(IProject project, int kind, IProgressMonitor monitor)
	{
		fIndex = getIndex(project);
	}

	protected Index getIndex(IProject project)
	{
		return IndexManager.getInstance().getIndex(getURI(project));
	}

	public void buildEnding(IProgressMonitor monitor)
	{
		fIndex = null;
	}

	public void buildFile(BuildContext context, IProgressMonitor monitor)
	{
		if (fIndex == null)
		{
			fIndex = getIndex(context.getProject());
		}

		List<IFileStoreIndexingParticipant> indexers = IndexManager.getInstance().getIndexParticipants(
				context.getName());
		for (IFileStoreIndexingParticipant indexer : indexers)
		{
			try
			{
				indexer.index(context, fIndex, monitor);
			}
			catch (CoreException e)
			{
				IdeLog.logError(BuildPathCorePlugin.getDefault(), e);
			}
		}
	}

	public void deleteFile(BuildContext context, IProgressMonitor monitor)
	{
		fIndex.remove(context.getURI());
	}

	private static URI getURI(IProject project)
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

}
