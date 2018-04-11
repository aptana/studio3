/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

import com.aptana.core.IMap;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.git.core.model.GitIndex;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoryManager;

/**
 * This class performs two functions: 1. Listen for changes of files under git repos in the workspace and then force an
 * async refresh of that repo's index. 2. Listen for new/imported projects and automatically attach our git support to
 * them (unless pref to do so is off).
 * 
 * @author cwilliams
 */
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
		{
			return;
		}

		Map<GitRepository, Set<IResource>> reposToRefresh = Collections.emptyMap();
		Set<IProject> projectsToAttach = Collections.emptySet();
		ResourceDeltaVisitor visitor = new ResourceDeltaVisitor();
		try
		{
			// Compute the changed resources by looking at the delta
			event.getDelta().accept(visitor, true /* includePhantoms */);
			reposToRefresh = visitor.getRepositoriestoRefresh();
			projectsToAttach = visitor.getProjectsToAttach();
		}
		catch (CoreException e)
		{
			IdeLog.logError(GitPlugin.getDefault(), e, IDebugScopes.DEBUG);
		}

		if (autoAttachGitRepos() && !projectsToAttach.isEmpty())
		{
			new GitAutoAttachProjectJob("Attaching Git repos", projectsToAttach).schedule(); //$NON-NLS-1$
		}

		if (reposToRefresh.isEmpty())
		{
			return;
		}

		for (Map.Entry<GitRepository, Set<IResource>> entry : reposToRefresh.entrySet())
		{
			final GitRepository repo = entry.getKey();
			if (repo == null)
			{
				continue;
			}

			Set<IResource> resources = entry.getValue();
			if (CollectionsUtil.isEmpty(resources) || dontRefresh(resources.iterator().next().getProject()))
			{
				continue;
			}
			GitIndex index = repo.index();
			if (index != null)
			{
				if (!CollectionsUtil.isEmpty(resources))
				{
					final List<IPath> filePaths = CollectionsUtil.map(resources, new IMap<IResource, IPath>()
					{
						public IPath map(IResource item)
						{
							return repo.relativePath(item);
						}
					});
					index.refreshAsync(filePaths); // queue up a refresh
				}
			}
		}
	}

	protected boolean dontRefresh(IProject project)
	{
		return !Platform.getPreferencesService().getBoolean(GitPlugin.getPluginId(),
				IPreferenceConstants.REFRESH_INDEX_WHEN_RESOURCES_CHANGE, true,
				new IScopeContext[] { new ProjectScope(project), InstanceScope.INSTANCE, DefaultScope.INSTANCE });
	}

	private boolean autoAttachGitRepos()
	{
		return Platform.getPreferencesService().getBoolean(GitPlugin.getPluginId(),
				IPreferenceConstants.AUTO_ATTACH_REPOS, true, null);
	}

	private IGitRepositoryManager getGitRepositoryManager()
	{
		return GitPlugin.getDefault().getGitRepositoryManager();
	}

	private GitRepository getRepo(IResource resource)
	{
		if (resource == null)
		{
			return null;
		}
		IProject project = resource.getProject();
		if (project == null)
		{
			return null;
		}
		return getGitRepositoryManager().getAttached(project);
	}

	/**
	 * Automatically attaches projects to their existing git repositories.
	 * 
	 * @author cwilliams
	 */
	private final class GitAutoAttachProjectJob extends Job
	{
		private Set<IProject> projectsToAttach;

		private GitAutoAttachProjectJob(String name, Set<IProject> projectsToAttach)
		{
			super(name);
			this.projectsToAttach = projectsToAttach;
			EclipseUtil.setSystemForJob(this);
			setPriority(Job.SHORT);
		}

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
					{
						getGitRepositoryManager().attachExisting(project, sub.newChild(10));
					}
				}
				catch (CoreException e)
				{
					multi.add(e.getStatus());
				}
			}
			return multi;
		}
	}

	/**
	 * Visits resource deltas and collects the set of repositories affected so we can refresh them. Also listens for new
	 * projects so we can auto-attach the git team provider to them.
	 * 
	 * @author cwilliams
	 */
	private final class ResourceDeltaVisitor implements IResourceDeltaVisitor
	{
		private Map<GitRepository, Set<IResource>> reposToRefresh;
		private Set<IProject> projectsToAttach;

		private ResourceDeltaVisitor()
		{
			reposToRefresh = new HashMap<GitRepository, Set<IResource>>();
			projectsToAttach = new HashSet<IProject>();
		}

		public Set<IProject> getProjectsToAttach()
		{
			return projectsToAttach;
		}

		public Map<GitRepository, Set<IResource>> getRepositoriestoRefresh()
		{
			return reposToRefresh;
		}

		public boolean visit(IResourceDelta delta)
		{

			// If the file has changed but not in a way that we care
			// about (e.g. marker changes to files) then ignore
			if (delta.getKind() == IResourceDelta.CHANGED && (delta.getFlags() & INTERESTING_CHANGES) == 0)
			{
				return true;
			}

			// Auto-attach to git if it's a new project being added and there's a repo and it's not already
			// attached
			IResource resource = delta.getResource();
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

			if (resource.getType() == IResource.ROOT)
			{
				// Continue with the delta
				return true;
			}

			if (resource.getType() == IResource.PROJECT)
			{
				// If the project is not accessible, don't process it
				if (!resource.isAccessible())
				{
					return false;
				}
			}

			// If the resource is not part of a project under Git
			// revision control
			GitRepository mapping = getRepo(resource);
			if (mapping == null)
			{
				// Ignore the change
				return true;
			}

			if (resource.isTeamPrivateMember())
			{
				return false;
			}

			// All seems good, schedule the repo for update.
			// TODO We force a refresh of the whole index for this repo. Maybe we should see if there's a way to
			// refresh the status of just this file?
			Set<IResource> resources = reposToRefresh.get(mapping);
			if (resources == null)
			{
				resources = new HashSet<IResource>();
			}
			resources.add(resource);
			reposToRefresh.put(mapping, resources);

			if (delta.getKind() == IResourceDelta.CHANGED && (delta.getFlags() & IResourceDelta.OPEN) > 1)
			{
				return false; // Don't recurse when opening projects
			}
			return true;
		}
	}
}
