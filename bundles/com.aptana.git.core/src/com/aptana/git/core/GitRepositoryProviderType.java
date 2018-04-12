/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core;

import java.text.MessageFormat;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.team.core.RepositoryProviderType;

import com.aptana.core.util.EclipseUtil;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoryManager;

public class GitRepositoryProviderType extends RepositoryProviderType
{

	@Override
	public void metaFilesDetected(IProject project, IContainer[] containers)
	{
		// FIXME What if the container isn't the project root!
		if (getGitRepositoryManager().getAttached(project) != null)
			return;

		if (autoAttachGitRepos() && hasGitDir(project))
		{
			final IProject toConnect = project;
			Job job = new Job(Messages.GitRepositoryProviderType_AutoShareJob_Title)
			{

				@Override
				protected IStatus run(IProgressMonitor monitor)
				{
					if (monitor == null)
						monitor = new NullProgressMonitor();
					monitor.beginTask(
							MessageFormat.format(Messages.GitRepositoryProviderType_AttachingProject_Message,
									toConnect.getName()), 100);
					try
					{
						getGitRepositoryManager().attachExisting(toConnect, new SubProgressMonitor(monitor, 100));
						monitor.done();
					}
					catch (CoreException e)
					{
						return e.getStatus();
					}
					return Status.OK_STATUS;
				}
			};
			EclipseUtil.setSystemForJob(job);
			job.schedule();
		}
	}

	protected boolean hasGitDir(IProject project)
	{
		final IResource dotGit = project.findMember(GitRepository.GIT_DIR);
		if (dotGit != null && dotGit.exists())
		{
			return true;
		}
		return false;
	}

	private boolean autoAttachGitRepos()
	{
		return Platform.getPreferencesService().getBoolean(GitPlugin.getPluginId(),
				IPreferenceConstants.AUTO_ATTACH_REPOS, true, null);
	}

	protected IGitRepositoryManager getGitRepositoryManager()
	{
		return GitPlugin.getDefault().getGitRepositoryManager();
	}
}
