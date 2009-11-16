package com.aptana.git.core;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.aptana.git.core.model.GitRepository;

class GitResourceListener implements IResourceChangeListener
{

	/**
	 * Bit-mask describing interesting changes for IResourceChangeListener events
	 */
	private static int INTERESTING_CHANGES = IResourceDelta.CONTENT | IResourceDelta.MOVED_FROM
			| IResourceDelta.MOVED_TO | IResourceDelta.OPEN | IResourceDelta.REPLACED | IResourceDelta.TYPE;

	/**
	 * Callback for IResourceChangeListener events Schedules a refresh of the changed resource If the preference for
	 * computing deep dirty states has been set we walk the ancestor tree of the changed resource and update all parents
	 * as well.
	 * 
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	public void resourceChanged(IResourceChangeEvent event)
	{
		final Set<GitRepository> resourcesToUpdate = new HashSet<GitRepository>();

		try
		{ // Compute the changed resources by looking at the delta
			event.getDelta().accept(new IResourceDeltaVisitor()
			{
				public boolean visit(IResourceDelta delta) throws CoreException
				{

					// If the file has changed but not in a way that we care
					// about (e.g. marker changes to files) then ignore
					if (delta.getKind() == IResourceDelta.CHANGED && (delta.getFlags() & INTERESTING_CHANGES) == 0)
					{
						return true;
					}

					final IResource resource = delta.getResource();

					// If the resource is not part of a project under Git
					// revision control
					final GitRepository mapping = getRepo(resource);
					if (mapping == null)
					{
						// Ignore the change
						return true;
					}

					if (resource.getType() == IResource.ROOT)
					{
						// Continue with the delta
						return true;
					}

					if (resource.getType() == IResource.PROJECT)
					{
						// If the project is not accessible, don't process it
						if (!resource.isAccessible())
							return false;
					}

					// All seems good, schedule the repo for update
					resourcesToUpdate.add(mapping);

					if (delta.getKind() == IResourceDelta.CHANGED && (delta.getFlags() & IResourceDelta.OPEN) > 1)
						return false; // Don't recurse when opening projects
					return true;
				}
			}, true /* includePhantoms */);
		}
		catch (final CoreException e)
		{
			GitPlugin.logError(e);
		}

		if (resourcesToUpdate.isEmpty())
			return;

		for (final GitRepository repo : resourcesToUpdate)
		{
			Job job = new Job("Updating Git repo index") //$NON-NLS-1$
			{
				@Override
				protected IStatus run(IProgressMonitor monitor)
				{
					repo.index().refresh();
					return Status.OK_STATUS;
				}
			};
			job.setSystem(true);
			job.setPriority(Job.SHORT);
			job.schedule();
		}
	}

	protected GitRepository getRepo(IResource resource)
	{
		if (resource == null)
			return null;
		IProject project = resource.getProject();
		if (project == null)
			return null;
		return GitRepository.getAttached(project);
	}
}
