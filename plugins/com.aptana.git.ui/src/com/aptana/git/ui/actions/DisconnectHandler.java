/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.actions;

import java.text.MessageFormat;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.team.core.TeamException;

import com.aptana.core.logging.IdeLog;
import com.aptana.git.ui.GitUIPlugin;
import com.aptana.git.ui.internal.actions.AbstractGitHandler;
import com.aptana.git.ui.internal.actions.Messages;

public class DisconnectHandler extends AbstractGitHandler
{

	private IJobChangeListener listener;

	public DisconnectHandler()
	{
	}

	public DisconnectHandler(IJobChangeListener listener)
	{
		this.listener = listener;
	}

	@Override
	protected Object doExecute(ExecutionEvent event) throws ExecutionException
	{
		for (IResource resource : getSelectedResources())
		{
			disconnect(resource.getProject(), listener);
		}
		return null;
	}

	/**
	 * @param project
	 */
	public static void disconnect(final IProject project, IJobChangeListener listener)
	{
		Job job = new WorkspaceJob(MessageFormat.format(Messages.DisconnectHandler_Job_Title, project.getName()))
		{

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException
			{
				SubMonitor sub = SubMonitor.convert(monitor, 100);

				// Set back all the team privates to false!
				try
				{
					project.accept(new IResourceProxyVisitor()
					{

						public boolean visit(IResourceProxy proxy) throws CoreException
						{
							if (proxy.isTeamPrivateMember())
							{
								proxy.requestResource().setTeamPrivateMember(false);
							}
							return true;
						}
					}, IContainer.INCLUDE_TEAM_PRIVATE_MEMBERS);
				}
				catch (CoreException e)
				{
					IdeLog.logError(GitUIPlugin.getDefault(), e);
				}
				sub.worked(20);

				try
				{
					RepositoryProvider.unmap(project);
				}
				catch (Exception e)
				{
					IdeLog.logError(GitUIPlugin.getDefault(), e);
				}
				sub.worked(10);

				getGitRepositoryManager().removeRepository(project);
				sub.worked(10);

				project.refreshLocal(IResource.DEPTH_INFINITE, sub.newChild(60));

				sub.done();

				return Status.OK_STATUS;
			}
		};
		if (listener != null)
		{
			job.addJobChangeListener(listener);
		}
		job.schedule();
	}
}
