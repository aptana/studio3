/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.actions;

import java.text.MessageFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

import com.aptana.core.util.ProcessStatus;
import com.aptana.core.util.StringUtil;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.github.IGithubManager;
import com.aptana.git.core.github.IGithubRepository;
import com.aptana.git.core.github.IGithubUser;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.GitRepository.ReadWrite;
import com.aptana.git.ui.dialogs.CreatePullRequestDialog;
import com.aptana.git.ui.internal.preferences.GithubAccountPageProvider;
import com.aptana.ui.util.UIUtils;

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
			throw new ExecutionException("No git repository selected");
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
				throw new ExecutionException("User is not logged into their Github account");
			}
		}

		// Ok, we have the name of the repo on github, the user has logged into github (or we know their credentials and
		// they work), now let's generate a pull request!
		IGithubRepository ghRepo;
		try
		{
			ghRepo = user.getRepo(ghRepoName);
		}
		catch (CoreException e)
		{
			throw new ExecutionException("Unable to get repository details from github API", e);
		}
		// Prompt for title and body!
		// Pre-populate title and body with details of commit log?

		// .git/logs/refs/heads/<branch_name> holds the log (sort of)
		// git log -g --pretty (when on HEAD of feature branch) shows commits
		// This assumes we're on current branch!
		IStatus commitsStatus = repo.execute(ReadWrite.READ, "log", "-g", "--pretty"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		// TODO Allow user to select different local and remote branch for PR?
		CreatePullRequestDialog id = new CreatePullRequestDialog(UIUtils.getActiveShell(), repo.currentBranch(),
				commitsStatus.isOK() ? commitsStatus.getMessage() : StringUtil.EMPTY);
		if (id.open() == Window.CANCEL)
		{
			return null;
		}

		final IStatus status = ghRepo.createPullRequest(id.getTitle(), id.getBody(), repo);
		if (!status.isOK())
		{
			throw new ExecutionException(status.getMessage());
		}
		// Now show a tooltip "toast" for 3 seconds to announce success
		final Shell shell = UIUtils.getActiveShell();
		DefaultToolTip toolTip = new DefaultToolTip(shell)
		{
			@Override
			public Point getLocation(Point size, Event event)
			{
				final Rectangle workbenchWindowBounds = shell.getBounds();
				int xCoord = workbenchWindowBounds.x + workbenchWindowBounds.width - size.x - 10;
				int yCoord = workbenchWindowBounds.y + workbenchWindowBounds.height - size.y - 10;
				return new Point(xCoord, yCoord);
			}
		};
		toolTip.setHideDelay(UIUtils.DEFAULT_TOOLTIP_TIME);
		toolTip.setText("Successfully generated pull request.");
		toolTip.show(new Point(0, 0));
		return null;
	}

	private String getGithubRepoName() throws ExecutionException
	{
		final GitRepository repo = getSelectedRepository();
		if (repo == null)
		{
			throw new ExecutionException("No git repository selected");
		}

		String remoteURL;
		try
		{
			Map<String, String> pairs = repo.remotePairs();
			remoteURL = pairs.get(GitRepository.ORIGIN);
			if (remoteURL == null)
			{
				// FIXME Loop through and find correct remote? If we find multiple do we need to prompt?
				throw new ExecutionException("Remote 'origin' not set up");
			}
		}
		catch (CoreException e)
		{
			throw new ExecutionException("Unable to get remotes for repository", e);
		}

		Pattern p = Pattern.compile(GITHUB_REMOTE_REGEX);
		Matcher m = p.matcher(remoteURL);
		if (!m.find())
		{
			throw new ExecutionException(MessageFormat.format("Unable to extract repo name from '{0}' remote url: {1}",
					GitRepository.ORIGIN, remoteURL));
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
