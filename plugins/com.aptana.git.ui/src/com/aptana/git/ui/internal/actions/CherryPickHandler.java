/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.actions;

import java.text.MessageFormat;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.team.ui.history.IHistoryPage;
import org.eclipse.team.ui.history.IHistoryView;
import org.eclipse.ui.ISources;

import com.aptana.core.logging.IdeLog;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.GitCommit;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoryManager;
import com.aptana.git.ui.GitUIPlugin;
import com.aptana.git.ui.internal.Launcher;
import com.aptana.git.ui.internal.history.GitHistoryPage;

public class CherryPickHandler extends AbstractHandler
{

	private GitCommit getCommit(IEvaluationContext evaluationContext)
	{
		ISelection selection = null;
		Object part = evaluationContext.getVariable(ISources.ACTIVE_PART_NAME);
		if (part instanceof IHistoryView)
		{
			IHistoryView view = (IHistoryView) part;
			IHistoryPage page = view.getHistoryPage();
			if (page instanceof GitHistoryPage)
			{
				GitHistoryPage ghp = (GitHistoryPage) page;
				selection = ghp.getSelectionProvider().getSelection();

				if (selection instanceof IStructuredSelection)
				{
					IStructuredSelection ss = (IStructuredSelection) selection;
					Object selected = ss.getFirstElement();
					if (selected instanceof GitCommit)
					{
						return (GitCommit) selected;
					}
				}
			}
		}
		return null;
	}

	protected IGitRepositoryManager getGitRepositoryManager()
	{
		return GitPlugin.getDefault().getGitRepositoryManager();
	}

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		if (event == null)
		{
			return null;
		}
		Object context = event.getApplicationContext();
		if (context instanceof IEvaluationContext)
		{
			GitCommit commit = getCommit((IEvaluationContext) context);
			cherryPick(commit);
		}
		return null;
	}

	private void cherryPick(final GitCommit commit)
	{
		Job job = new Job(MessageFormat.format("git cherry-pick {0}", commit.sha())) //$NON-NLS-1$
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				SubMonitor sub = SubMonitor.convert(monitor, 1000);
				if (sub.isCanceled())
				{
					return Status.CANCEL_STATUS;
				}

				GitRepository currentRepo = commit.repository();
				currentRepo.enterWriteProcess();
				try
				{
					ILaunch launch = Launcher.launch(currentRepo, sub.newChild(100), "cherry-pick", commit.sha()); //$NON-NLS-1$

					if (launch.getProcesses() == null || launch.getProcesses().length < 1)
					{
						// If something went wrong and there's no process (like unsaved files and user cancelled
						// dialog)
						return Status.OK_STATUS;
					}

					IProcess process = launch.getProcesses()[0];
					while (!launch.isTerminated())
					{
						Thread.sleep(1);
						if (sub.isCanceled())
						{
							launch.terminate();
							return Status.CANCEL_STATUS;
						}
						sub.worked(1);
					}

					int exitValue = process.getExitValue();
					if (exitValue != 0)
					{
						String msg = MessageFormat
								.format("cherry-pick {0} returned non-zero exit value. wd: {1}", commit.sha(), currentRepo.workingDirectory()); //$NON-NLS-1$
						IdeLog.logWarning(GitUIPlugin.getDefault(), msg);
					}
					commit.refreshAffectedFiles();
				}
				catch (CoreException e)
				{
					return e.getStatus();
				}
				catch (Throwable e)
				{
					return new Status(IStatus.ERROR, GitUIPlugin.getPluginId(), e.getMessage(), e);
				}
				finally
				{
					currentRepo.exitWriteProcess();
				}
				sub.done();

				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.LONG);
		job.schedule();
	}
}
