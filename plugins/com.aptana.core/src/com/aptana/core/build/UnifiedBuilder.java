package com.aptana.core.build;

import java.net.URI;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.core.resources.IMarkerConstants;
import com.aptana.index.core.IndexFilesOfProjectJob;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexPlugin;
import com.aptana.index.core.RebuildIndexJob;
import com.aptana.index.core.RemoveIndexOfFilesOfProjectJob;

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
		SubMonitor sub = SubMonitor.convert(monitor, 2);
		removeProblemsAndTasksFor(getProject());
		sub.worked(1);
		URI uri = getURI();
		if (uri != null)
		{
			IndexManager.getInstance().removeIndex(uri);
		}
		sub.done();
	}

	private URI getURI()
	{
		URI uri = getProject().getLocationURI();
		if (uri != null)
		{
			return uri;
		}
		IndexPlugin.logError(MessageFormat.format("Project's location URI is null. raw location: {0}, path: {1}", //$NON-NLS-1$
				getProject().getRawLocationURI(), getProject().getFullPath()), null);
		uri = getProject().getRawLocationURI();
		return uri;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException
	{
		if (kind == IncrementalProjectBuilder.FULL_BUILD)
		{
			fullBuild(monitor);
		}
		else
		{
			IResourceDelta delta = getDelta(getProject());
			if (delta == null)
			{
				fullBuild(monitor);
			}
			else
			{
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}

	private void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 4);
		if (delta != null)
		{
			ResourceCollector resourceCollector = new ResourceCollector();
			try
			{
				delta.accept(resourceCollector);
				sub.worked(1);
				if (!resourceCollector.filesToRemoveFromIndex.isEmpty())
				{
					RemoveIndexOfFilesOfProjectJob removeJob = new RemoveIndexOfFilesOfProjectJob(getProject(),
							resourceCollector.filesToRemoveFromIndex);
					removeJob.run(sub.newChild(1));
				}
				sub.setWorkRemaining(2);
				if (!resourceCollector.filesToIndex.isEmpty())
				{
					IndexFilesOfProjectJob indexJob = new IndexFilesOfProjectJob(getProject(),
							resourceCollector.filesToIndex);
					indexJob.run(sub.newChild(2));
				}
			}
			catch (CoreException e)
			{
				IndexPlugin.logError(e);
			}
		}
		sub.done();
	}

	private void fullBuild(IProgressMonitor monitor) throws CoreException
	{
		// Remove all markers/tasks? Index participants seem to do this for themselves!
		// FIXME Run rebuild index, or IndexProject?
		RebuildIndexJob job = new RebuildIndexJob(getURI());
		job.run(monitor);
	}

	private static class ResourceCollector implements IResourceDeltaVisitor
	{
		Set<IFile> filesToIndex = new HashSet<IFile>();
		Set<IFile> filesToRemoveFromIndex = new HashSet<IFile>();

		public boolean visit(IResourceDelta delta) throws CoreException
		{
			// TODO Collect all the changes and translate them into adding/removing files
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
