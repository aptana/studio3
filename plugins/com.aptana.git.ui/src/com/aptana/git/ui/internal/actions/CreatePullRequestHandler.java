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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.github.IGithubManager;
import com.aptana.git.core.github.IGithubPullRequest;
import com.aptana.git.core.github.IGithubRepository;
import com.aptana.git.core.github.IGithubUser;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.GitRepository.ReadWrite;
import com.aptana.git.ui.GitUIPlugin;
import com.aptana.git.ui.dialogs.CreatePullRequestDialog;
import com.aptana.git.ui.internal.preferences.GithubAccountPageProvider;
import com.aptana.ui.dialogs.HyperlinkInfoPopupDialog;
import com.aptana.ui.util.UIUtils;
import com.aptana.ui.util.WorkbenchBrowserUtil;

/**
 * @author cwilliams
 */
public class CreatePullRequestHandler extends AbstractGitHandler
{
	/**
	 * The regexp used to parse out the repo name from a remote pointing at github
	 */
	private static final String GITHUB_REMOTE_REGEX = ".+?github\\.com:[^/]+?/([\\w\\-_]+)\\.git"; //$NON-NLS-1$

	@Override
	protected Object doExecute(ExecutionEvent event) throws ExecutionException
	{
		final GitRepository repo = getSelectedRepository();
		if (repo == null)
		{
			throw new ExecutionException(Messages.CreatePullRequestHandler_NoRepoErr);
		}
		String ghRepoName = getGithubRepoName();

		// Make sure user is logged in!
		IGithubManager ghManager = GitPlugin.getDefault().getGithubManager();
		IGithubUser user = ghManager.getUser();
		if (user == null)
		{
			// prompt for login!
			final GithubAccountPageProvider userInfoProvider = new GithubAccountPageProvider();
			Dialog dialog = new Dialog(UIUtils.getActiveShell())
			{
				@Override
				protected Control createDialogArea(Composite parent)
				{
					Composite composite = (Composite) super.createDialogArea(parent);
					Control userInfoControl = userInfoProvider.createContents(composite);
					userInfoControl.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(3, 1).create());
					return composite;
				}

				@Override
				protected void okPressed()
				{
					if (!userInfoProvider.performOk())
					{
						// TODO Show an error message that we were unable to login?
						return;
					}
					super.okPressed();
				}
			};
			if (dialog.open() == Window.CANCEL)
			{
				return null; // User cancelled
			}

			user = ghManager.getUser();
			if (user == null)
			{
				UIUtils.showErrorMessage(Messages.CreatePullRequestHandler_PullRequestTitle,
						Messages.CreatePullRequestHandler_NotLoggedInErr);
				throw new ExecutionException(Messages.CreatePullRequestHandler_NotLoggedInErr);
			}
		}

		// Ok, we have the name of the repo on github, the user has logged into github (or we know their credentials and
		// they work), now let's generate a pull request!
		final IGithubRepository ghRepo;
		try
		{
			ghRepo = user.getRepo(ghRepoName);
		}
		catch (CoreException e)
		{
			UIUtils.showErrorMessage(Messages.CreatePullRequestHandler_PullRequestTitle,
					Messages.CreatePullRequestHandler_RepoAPIErr);
			throw new ExecutionException(Messages.CreatePullRequestHandler_RepoAPIErr, e);
		}
		// Prompt for title and body!
		// Pre-populate title and body with details of commit log?

		// .git/logs/refs/heads/<branch_name> holds the log (sort of)
		// git log -g --pretty (when on HEAD of feature branch) shows commits
		// This assumes we're on current branch!
		IStatus commitsStatus = repo.execute(ReadWrite.READ, "log", "-g", "--pretty"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		String branch = repo.currentBranch();
		String parentName = Messages.CreatePullRequestHandler_UnknownParentRepoOwnerName;
		String parentBranch = branch;
		try
		{
			IGithubRepository parentRepo = ghRepo.getParent();
			parentName = parentRepo.getOwner();
			parentBranch = parentRepo.getDefaultBranch();
		}
		catch (CoreException e2)
		{
			IdeLog.logWarning(GitUIPlugin.getDefault(),
					MessageFormat.format("Failed to get name of parent repo for repo: {0}", ghRepo)); //$NON-NLS-1$
		}
		String base = parentName + ':' + parentBranch;
		String head = user.getUsername() + ':' + branch;
		// TODO Allow user to select different local and remote branch for PR?
		CreatePullRequestDialog id = new CreatePullRequestDialog(UIUtils.getActiveShell(), branch,
				commitsStatus.isOK() ? commitsStatus.getMessage() : StringUtil.EMPTY, base, head);
		if (id.open() == Window.CANCEL)
		{
			return null;
		}

		final String title = id.getTitle();
		final String body = id.getBody();
		Job job = new Job(Messages.CreatePullRequestHandler_SubmitPRJobName)
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				try
				{
					final IGithubPullRequest pr = ghRepo.createPullRequest(title, body, repo, monitor);
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
											if (!StringUtil.isEmpty(e.text))
											{
												WorkbenchBrowserUtil.launchExternalBrowser(e.text);
											}
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

	private String getGithubRepoName() throws ExecutionException
	{
		final GitRepository repo = getSelectedRepository();
		if (repo == null)
		{
			throw new ExecutionException(Messages.CreatePullRequestHandler_NoRepoErr);
		}

		String remoteURL;
		try
		{
			Map<String, String> pairs = repo.remotePairs();
			remoteURL = pairs.get(GitRepository.ORIGIN);
			if (remoteURL == null)
			{
				// FIXME Loop through and find correct remote? If we find multiple do we need to prompt?
				throw new ExecutionException(Messages.CreatePullRequestHandler_RemoteOriginDoesntExistErr);
			}
		}
		catch (CoreException e)
		{
			throw new ExecutionException(Messages.CreatePullRequestHandler_GetRemotesFailedErr, e);
		}

		Pattern p = Pattern.compile(GITHUB_REMOTE_REGEX);
		Matcher m = p.matcher(remoteURL);
		if (!m.find())
		{
			throw new ExecutionException(MessageFormat.format(
					Messages.CreatePullRequestHandler_ExtractRepoNameFromRemoteFailedErr, GitRepository.ORIGIN,
					remoteURL));
		}
		return m.group(1);
	}

	@Override
	protected boolean calculateEnabled()
	{
		try
		{
			return getGithubRepoName() != null;
		}
		catch (ExecutionException e)
		{
			return false;
		}
	}
}
