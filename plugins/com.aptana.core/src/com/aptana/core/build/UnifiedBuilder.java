/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.build;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.core.CorePlugin;
import com.aptana.core.IDebugScopes;
import com.aptana.core.internal.build.BuildParticipantManager;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.resources.IMarkerConstants;

public class UnifiedBuilder extends IncrementalProjectBuilder
{

	public static final String ID = "com.aptana.ide.core.unifiedBuilder"; //$NON-NLS-1$

	public UnifiedBuilder()
	{
	}

	private static void removeProblemsAndTasksFor(IResource resource)
	{
		try
		{
			if (resource != null && resource.exists())
			{
				resource.deleteMarkers(IMarkerConstants.PROBLEM_MARKER, true, IResource.DEPTH_INFINITE);
				resource.deleteMarkers(IMarkerConstants.TASK_MARKER, true, IResource.DEPTH_INFINITE);
			}
		}
		catch (CoreException e)
		{
			// assume there were no problems
		}
	}

	@Override
	protected void clean(IProgressMonitor monitor) throws CoreException
	{
		super.clean(monitor);

		List<IBuildParticipant> participants = BuildParticipantManager.getInstance().getBuildParticipants();
		SubMonitor sub = SubMonitor.convert(monitor, participants.size() + 1);

		IProject project = getProject();
		removeProblemsAndTasksFor(project);
		sub.worked(1);

		for (IBuildParticipant participant : participants)
		{
			participant.clean(project, sub.newChild(1));
		}
		sub.done();
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException
	{
		String projectName = getProject().getName();
		long startTime = System.nanoTime();

		if (kind == IncrementalProjectBuilder.FULL_BUILD)
		{
			if (IdeLog.isInfoEnabled(CorePlugin.getDefault(), IDebugScopes.BUILDER))
			{
				// @formatter:off
				IdeLog.logInfo(
					CorePlugin.getDefault(),
					MessageFormat.format(Messages.UnifiedBuilder_PerformingFullBuld, projectName),
					IDebugScopes.BUILDER
				);
				// @formatter:on
			}
			fullBuild(monitor);
		}
		else
		{
			IResourceDelta delta = getDelta(getProject());
			if (delta == null)
			{
				// @formatter:off
				IdeLog.logInfo(
					CorePlugin.getDefault(),
					MessageFormat.format(Messages.UnifiedBuilder_PerformingFullBuildNullDelta, projectName),
					IDebugScopes.BUILDER
				);
				// @formatter:on
				fullBuild(monitor);
			}
			else
			{
				// @formatter:off
				IdeLog.logInfo(
					CorePlugin.getDefault(),
					MessageFormat.format(Messages.UnifiedBuilder_PerformingIncrementalBuild, projectName),
					IDebugScopes.BUILDER
				);
				// @formatter:on
				incrementalBuild(delta, monitor);
			}
		}

		double endTime = ((double) System.nanoTime() - startTime) / 1000000;
		// @formatter:off
		IdeLog.logInfo(
			CorePlugin.getDefault(),
			MessageFormat.format(Messages.UnifiedBuilder_FinishedBuild, projectName, endTime),
			IDebugScopes.BUILDER
		);
		// @formatter:on

		return null;
	}

	private void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor)
	{
		List<IBuildParticipant> participants = BuildParticipantManager.getInstance().getBuildParticipants();
		for (IBuildParticipant participant : participants)
		{
			participant.incrementalBuild(delta, getProject(), monitor);
		}
	}

	private void fullBuild(IProgressMonitor monitor) throws CoreException
	{
		// Remove all markers/tasks? Index participants seem to do this for themselves!
		List<IBuildParticipant> participants = BuildParticipantManager.getInstance().getBuildParticipants();
		for (IBuildParticipant participant : participants)
		{
			participant.fullBuild(getProject(), monitor);
		}
	}
}
