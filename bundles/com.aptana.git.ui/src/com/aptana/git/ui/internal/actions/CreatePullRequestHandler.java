/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.actions;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.git.core.github.IGithubPullRequest;
import com.aptana.git.core.github.IGithubRepository;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.GitRepository.ReadWrite;
import com.aptana.git.ui.GitUIPlugin;
import com.aptana.git.ui.dialogs.CreatePullRequestDialog;
import com.aptana.ui.dialogs.HyperlinkInfoPopupDialog;
import com.aptana.ui.util.UIUtils;

/**
 * @author cwilliams
 */
public class CreatePullRequestHandler extends AbstractGithubHandler
{
	@Override
	protected Object doExecute(ExecutionEvent event) throws ExecutionException
	{
		final GitRepository repo = getSelectedRepository();
		if (repo == null)
		{
			throw new ExecutionException(Messages.CreatePullRequestHandler_NoRepoErr);
		}

		IGithubRepository ghRepo = getGithubRepo();
		if (ghRepo == null)
		{
			return null;
		}

		Set<IGithubRepository> repos = CollectionsUtil.newSet(ghRepo);
		IGithubRepository defaultBaseRepo = ghRepo;
		// By default set the base repo to be the parent of the current repo
		try
		{
			defaultBaseRepo = ghRepo.getParent();
			if (defaultBaseRepo != null)
			{
				repos.add(defaultBaseRepo);
			}
		}
		catch (CoreException e)
		{
			// ignore
		}

		// Now try to gather all the forks
		try
		{
			IGithubRepository source = ghRepo.getSource();
			if (source != null)
			{
				repos.add(source);
				repos.addAll(source.getForks());
			}
		}
		catch (CoreException e2)
		{
			// ignore
		}

		// Prompt for title and body!
		// Pre-populate title and body with details of commit log?

		// .git/logs/refs/heads/<branch_name> holds the log (sort of)
		// git log -g --pretty (when on HEAD of feature branch) shows commits
		// This assumes we're on current branch!
		IStatus commitsStatus = repo.execute(ReadWrite.READ, "log", "-g", "--pretty"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		String branch = repo.currentBranch();

		String head = ghRepo.getOwner() + ':' + branch;
		// TODO Allow user to select different local and remote branch for PR?
		CreatePullRequestDialog id = new CreatePullRequestDialog(UIUtils.getActiveShell(), branch,
				commitsStatus.isOK() ? commitsStatus.getMessage() : StringUtil.EMPTY, defaultBaseRepo, repos, head);
		if (id.open() == Window.CANCEL)
		{
			return null;
		}

		final String title = id.getTitle();
		final String body = id.getBody();
		final String baseBranch = id.getBaseBranch();
		final IGithubRepository baseRepo = id.getBaseRepo();
		final IGithubRepository theGhRepo = ghRepo;
		Job job = new Job(Messages.CreatePullRequestHandler_SubmitPRJobName)
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				try
				{
					final IGithubPullRequest pr = theGhRepo.createPullRequest(title, body, repo, baseRepo, baseBranch,
							monitor);
					// Ok we submitted a PR, let's show a popup/toast to let user know and let them click it to open it.
					URL url = null;
					try
					{
						url = pr.getHTMLURL();
					}
					catch (MalformedURLException e1)
					{
						IdeLog.logError(GitUIPlugin.getDefault(), e1);
					}
					final String prURL = (url == null) ? StringUtil.EMPTY : url.toString();
					UIUtils.getDisplay().asyncExec(new Runnable()
					{

						public void run()
						{
							HyperlinkInfoPopupDialog toolTip = new HyperlinkInfoPopupDialog(UIUtils.getActiveShell(),
									Messages.CreatePullRequestHandler_PRSubmittedTitle, MessageFormat.format(
											Messages.CreatePullRequestHandler_SuccessMsg, prURL, pr.getNumber()),
									new SelectionAdapter()
									{
										public void widgetSelected(SelectionEvent e)
										{
											viewPullRequest(pr);
										}
									});
							toolTip.open();
						}
					});
				}
				catch (CoreException e)
				{
					return e.getStatus();
				}
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();

		return null;
	}
}
