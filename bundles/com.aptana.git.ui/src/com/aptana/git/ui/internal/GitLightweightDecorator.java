/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.team.ui.ISharedImages;
import org.eclipse.team.ui.TeamImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

import com.aptana.core.util.EclipseUtil;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.BranchAddedEvent;
import com.aptana.git.core.model.BranchChangedEvent;
import com.aptana.git.core.model.BranchRemovedEvent;
import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoriesListener;
import com.aptana.git.core.model.IGitRepositoryListener;
import com.aptana.git.core.model.IGitRepositoryManager;
import com.aptana.git.core.model.IndexChangedEvent;
import com.aptana.git.core.model.PullEvent;
import com.aptana.git.core.model.PushEvent;
import com.aptana.git.core.model.RepositoryAddedEvent;
import com.aptana.git.core.model.RepositoryRemovedEvent;
import com.aptana.git.ui.GitUIPlugin;
import com.aptana.theme.IThemeManager;
import com.aptana.theme.ThemePlugin;

public class GitLightweightDecorator extends BaseLabelProvider implements ILightweightLabelDecorator,
		IGitRepositoryListener, IGitRepositoriesListener
{

	public static final String UNTRACKED_IMAGE = "icons/ovr/untracked.gif"; //$NON-NLS-1$
	public static final String STAGED_ADDED_IMAGE = "icons/ovr/staged_added.gif"; //$NON-NLS-1$
	public static final String STAGED_REMOVED_IMAGE = "icons/ovr/staged_removed.gif"; //$NON-NLS-1$

	private static final String DIRTY_PREFIX = "* "; //$NON-NLS-1$
	private static final String DECORATOR_ID = "com.aptana.git.ui.internal.GitLightweightDecorator"; //$NON-NLS-1$

	private static ImageDescriptor conflictImage;
	private static UIJob refreshJob;

	private IPreferenceChangeListener fThemeChangeListener;
	private Map<RepoBranch, TimestampedString> cache;

	public GitLightweightDecorator()
	{
		cache = new HashMap<RepoBranch, TimestampedString>();
		IGitRepositoryManager manager = getGitRepositoryManager();
		if (manager != null)
		{
			manager.addListener(this);
			manager.addListenerToEachRepository(this);
		}
		fThemeChangeListener = new IPreferenceChangeListener()
		{

			public void preferenceChange(PreferenceChangeEvent event)
			{
				if (event.getKey().equals(IThemeManager.THEME_CHANGED))
				{
					refresh();
				}
			}
		};
		InstanceScope.INSTANCE.getNode(ThemePlugin.PLUGIN_ID).addPreferenceChangeListener(fThemeChangeListener);
	}

	protected IGitRepositoryManager getGitRepositoryManager()
	{
		GitPlugin plugin = GitPlugin.getDefault();
		return plugin == null ? null : plugin.getGitRepositoryManager();
	}

	public void decorate(Object element, IDecoration decoration)
	{
		final IResource resource = getResource(element);
		if (resource == null)
			return;

		// Don't decorate if the workbench is not running
		if (!isWorkbenchRunning())
			return;

		// Don't decorate if UI plugin is not running
		if (!isGitUIPluginActive())
			return;

		// Don't decorate the workspace root
		if (resource.getType() == IResource.ROOT)
			return;

		// Don't decorate non-existing resources
		if (!resource.exists() && !resource.isPhantom())
			return;

		switch (resource.getType())
		{
			case IResource.PROJECT:
				decorateProject(decoration, resource);
				//$FALL-THROUGH$
			case IResource.FOLDER: // $codepro.audit.disable nonTerminatedCaseClause
				decorateFolder(decoration, resource);
				break;
			case IResource.FILE:
				decorateFile(decoration, resource);
				break;
		}
	}

	protected boolean isGitUIPluginActive()
	{
		return GitUIPlugin.getDefault() != null;
	}

	protected boolean isWorkbenchRunning()
	{
		return PlatformUI.isWorkbenchRunning();
	}

	private void decorateFolder(IDecoration decoration, IResource resource)
	{
		GitRepository repo = getRepo(resource);
		if (repo == null)
			return;

		if (repo.resourceOrChildHasChanges(resource))
		{
			decoration.addPrefix(DIRTY_PREFIX);
		}
	}

	private void decorateFile(IDecoration decoration, final IResource resource)
	{
		IFile file = (IFile) resource;
		GitRepository repo = getRepo(resource);
		if (repo == null)
			return;

		ChangedFile changed = repo.getChangedFileForResource(file);
		if (changed == null)
		{
			return;
		}

		ImageDescriptor overlay = null;
		// Unstaged trumps staged when decorating. One file may have both staged and unstaged changes.
		if (changed.hasUnstagedChanges())
		{
			decoration.setForegroundColor(GitColors.redFG());
			decoration.setBackgroundColor(GitColors.redBG());
			if (changed.getStatus() == ChangedFile.Status.NEW)
			{
				overlay = untrackedImage();
			}
			else if (changed.getStatus() == ChangedFile.Status.UNMERGED)
			{
				overlay = conflictImage();
			}
		}
		else if (changed.hasStagedChanges())
		{
			decoration.setForegroundColor(GitColors.greenFG());
			decoration.setBackgroundColor(GitColors.greenBG());
			if (changed.getStatus() == ChangedFile.Status.DELETED)
			{
				overlay = stagedRemovedImage();
			}
			else if (changed.getStatus() == ChangedFile.Status.NEW)
			{
				overlay = stagedAddedImage();
			}
		}
		decoration.addPrefix(DIRTY_PREFIX);
		if (overlay != null)
			decoration.addOverlay(overlay);
	}

	private ImageDescriptor conflictImage()
	{
		if (conflictImage == null)
		{
			conflictImage = new CachedImageDescriptor(TeamImages.getImageDescriptor(ISharedImages.IMG_CONFLICT_OVR));
		}
		return conflictImage;
	}

	private ImageDescriptor stagedRemovedImage()
	{
		return GitUIPlugin.getDefault().getImageRegistry().getDescriptor(STAGED_REMOVED_IMAGE);
	}

	private ImageDescriptor stagedAddedImage()
	{
		return GitUIPlugin.getDefault().getImageRegistry().getDescriptor(STAGED_ADDED_IMAGE);
	}

	private ImageDescriptor untrackedImage()
	{
		return GitUIPlugin.getDefault().getImageRegistry().getDescriptor(UNTRACKED_IMAGE);
	}

	private void decorateProject(IDecoration decoration, final IResource resource)
	{
		GitRepository repo = getRepo(resource);
		if (repo == null)
		{
			return;
		}

		String branch = repo.currentBranch();
		// Adds a temporal cache per repo/branch for this data so we
		// don't recalculate for a ton of projects, Just store it for like a second...?
		RepoBranch repoBranch = new RepoBranch(repo, branch);
		TimestampedString result = cache.get(repoBranch);
		if (result != null && !result.isOlderThan(1000))
		{
			decoration.addSuffix(result.string);
			return;
		}
		cache.remove(repoBranch);

		StringBuilder builder = new StringBuilder();
		builder.append(" ["); //$NON-NLS-1$
		builder.append(branch);
		String[] commits = repo.commitsAhead(branch);
		if (commits != null && commits.length > 0)
		{
			builder.append('+').append(commits.length);
		}
		else
		{
			// Happens way less frequently. usually only if you've fetched but haven't merged (which usually happens
			// when you pull on one branch and then switch back to another that had changes not yet merged in yet)
			commits = repo.commitsBehind(branch);
			if (commits != null && commits.length > 0)
				builder.append('-').append(commits.length);
		}
		builder.append(']');
		String value = builder.toString();
		cache.put(repoBranch, new TimestampedString(value));
		decoration.addSuffix(value);
	}

	@Override
	public void dispose()
	{
		try
		{
			getGitRepositoryManager().removeListener(this);
			getGitRepositoryManager().removeListenerFromEachRepository(this);
			InstanceScope.INSTANCE.getNode(ThemePlugin.PLUGIN_ID).removePreferenceChangeListener(fThemeChangeListener);
			cache.clear();
		}
		finally
		{
			super.dispose();
		}
	}

	private static IResource getResource(Object element)
	{

		IResource resource = null;
		if (element instanceof IResource)
		{
			resource = (IResource) element;
		}
		else if (element instanceof IAdaptable)
		{
			final IAdaptable adaptable = (IAdaptable) element;
			resource = (IResource) adaptable.getAdapter(IResource.class);
		}
		return resource;
	}

	protected GitRepository getRepo(IResource resource)
	{
		if (resource == null)
			return null;
		IProject project = resource.getProject();
		if (project == null)
			return null;
		return getGitRepositoryManager().getAttached(project);
	}

	/**
	 * Post the label event to the UI thread
	 * 
	 * @param event
	 *            The event to post
	 */
	private void postLabelEvent(final LabelProviderChangedEvent event)
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{
				fireLabelProviderChanged(event);
			}
		});
	}

	public void indexChanged(IndexChangedEvent e)
	{
		// TODO Force a total refresh if the number of changed files is over some maximum?
		Set<IResource> resources = e.getFilesWithChanges();

		// Need to mark all parents up to project for refresh so the dirty flag can get recomputed for these
		// ancestor folders!
		resources.addAll(getAllAncestors(resources));
		// TODO On a commit clear the cache?
		// FIXME Only add projects if this was a commit (so the plus/minus changes), not just a file
		// edited/staged/unstaged
		// Also refresh any project sharing this repo (so the +/- commits ahead can be refreshed)
		for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects())
		{
			GitRepository repo = getGitRepositoryManager().getAttached(project);
			if (repo != null && repo.equals(e.getRepository()))
			{
				resources.add(project);
			}
		}
		postLabelEvent(new LabelProviderChangedEvent(this, resources.toArray()));
	}

	private Collection<? extends IResource> getAllAncestors(Set<IResource> resources)
	{
		Collection<IResource> ancestors = new HashSet<IResource>();
		for (IResource resource : resources)
		{
			IResource child = resource;
			IContainer parent = null;
			while ((parent = child.getParent()) != null) // $codepro.audit.disable assignmentInCondition
			{
				if (parent.getType() == IResource.PROJECT || parent.getType() == IResource.ROOT)
				{
					break;
				}
				ancestors.add(parent);
				child = parent;
			}
		}
		return ancestors;
	}

	public void repositoryAdded(RepositoryAddedEvent e)
	{
		e.getRepository().addListener(this);
		Set<IResource> resources = e.getRepository().getChangedResources();
		resources.add(e.getProject());
		postLabelEvent(new LabelProviderChangedEvent(this, resources.toArray()));
	}

	public void repositoryRemoved(RepositoryRemovedEvent e)
	{
		e.getRepository().removeListener(this);
	}

	/**
	 * Perform a blanket refresh of all decorations. This is very bad performance wise. Need to avoid using this and
	 * always just use deltas if possible!
	 */
	private static void refresh()
	{
		if (refreshJob == null)
		{
			refreshJob = new UIJob("Refresh Git labels") //$NON-NLS-1$
			{

				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					if (monitor != null && monitor.isCanceled())
						return Status.CANCEL_STATUS;
					GitUIPlugin.getDefault().getWorkbench().getDecoratorManager().update(DECORATOR_ID);
					return Status.OK_STATUS;
				}
			};
			EclipseUtil.setSystemForJob(refreshJob);
		}
		refreshJob.cancel();
		refreshJob.schedule(50);
	}

	public void branchChanged(BranchChangedEvent e)
	{
		Set<IResource> resources = new HashSet<IResource>();
		GitRepository repo = e.getRepository();
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject project : projects)
		{
			if (repo.equals(getGitRepositoryManager().getAttached(project)))
				resources.add(project);
		}
		// Project labels need to change, but the dirty/stage/unstaged flags should stay same (can't change branch with
		// staged/unstaged changes, dirty carry over).
		postLabelEvent(new LabelProviderChangedEvent(this, resources.toArray()));
	}

	public void pulled(PullEvent e)
	{
		cache.clear();
		refresh();
	}

	public void pushed(PushEvent e)
	{
		cache.clear();
		refresh();
	}

	public void branchAdded(BranchAddedEvent e)
	{
		// do nothing
	}

	public void branchRemoved(BranchRemovedEvent e)
	{
		// do nothing
	}

	// Simple classes used for a time-based cache on the project decorations

	private static class TimestampedString
	{
		String string;
		Long timestamp;

		TimestampedString(String value)
		{
			this.string = value;
			this.timestamp = System.currentTimeMillis();
		}

		public boolean isOlderThan(int millis)
		{
			return (timestamp + millis) < System.currentTimeMillis();
		}

		@Override
		public boolean equals(Object obj)
		{
			if (obj == this)
			{
				return true;
			}
			if (!(obj instanceof TimestampedString))
			{
				return false;
			}
			TimestampedString other = (TimestampedString) obj;
			return other.string.equals(string) && other.timestamp.equals(timestamp);
		}

		@Override
		public int hashCode()
		{
			return (31 + string.hashCode()) * (31 + timestamp.hashCode());
		}
	}

	private static class RepoBranch
	{
		GitRepository repo;
		String branch;

		RepoBranch(GitRepository repo, String branch)
		{
			this.repo = repo;
			this.branch = branch;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (obj == this)
			{
				return true;
			}
			if (!(obj instanceof RepoBranch))
			{
				return false;
			}
			RepoBranch other = (RepoBranch) obj;
			return other.repo.equals(repo) && other.branch.equals(branch);
		}

		@Override
		public int hashCode()
		{
			return (31 + repo.hashCode()) * (31 + branch.hashCode());
		}
	}
}
