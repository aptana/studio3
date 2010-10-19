/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
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
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;

import com.aptana.git.core.model.GitIndex;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoryManager;

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
		if (event == null || event.getDelta() == null)
			return;
		final Set<GitRepository> resourcesToUpdate = new HashSet<GitRepository>();
		final Set<IProject> projectsToAttach = new HashSet<IProject>();

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

					// Auto-attach to git if it's a new project being added and there's a repo and it's not already
					// attached
					final IResource resource = delta.getResource();
					if (resource != null && resource instanceof IProject && delta.getKind() == IResourceDelta.ADDED)
					{
						final GitRepository mapping = getRepo(resource);
						IProject project = (IProject) resource;
						if (mapping == null)
						{
							projectsToAttach.add(project);
							return false;
						}
					}

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

					// All seems good, schedule the repo for update.
					// TODO We force a refresh of the whole index for this repo. Maybe we should see if there's a way to
					// refresh the status of just this file?
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

		if (!projectsToAttach.isEmpty())
		{
			Job job = new Job("Attaching Git repos") //$NON-NLS-1$
			{
				@Override
				protected IStatus run(IProgressMonitor monitor)
				{
					SubMonitor sub = SubMonitor.convert(monitor, 10 * projectsToAttach.size());
					MultiStatus multi = new MultiStatus(GitPlugin.getPluginId(), 0, null, null);
					multi.add(Status.OK_STATUS);
					for (final IProject project : projectsToAttach)
					{
						try
						{
							if (project.isAccessible())
								getGitRepositoryManager().attachExisting(project, sub.newChild(10));
						}
						catch (CoreException e)
						{
							multi.add(e.getStatus());
						}
					}
					return multi;
				}
			};
			job.setSystem(true);
			job.setPriority(Job.SHORT);
			job.schedule();
		}

		if (resourcesToUpdate.isEmpty())
			return;

		for (final GitRepository repo : resourcesToUpdate)
		{
			if (repo == null)
				continue;
			GitIndex index = repo.index();
			if (index != null)
				index.refreshAsync(); // queue up a refresh
		}
	}

	private IGitRepositoryManager getGitRepositoryManager()
	{
		return GitPlugin.getDefault().getGitRepositoryManager();
	}

	private GitRepository getRepo(IResource resource)
	{
		if (resource == null)
			return null;
		IProject project = resource.getProject();
		if (project == null)
			return null;
		return getGitRepositoryManager().getAttached(project);
	}
}
