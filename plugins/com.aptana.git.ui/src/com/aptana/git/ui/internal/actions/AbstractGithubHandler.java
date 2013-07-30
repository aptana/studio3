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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.git.core.github.IGithubManager;
import com.aptana.git.core.github.IGithubPullRequest;
import com.aptana.git.core.github.IGithubRepository;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.GitUIPlugin;
import com.aptana.git.ui.internal.preferences.GithubAccountPageProvider;
import com.aptana.ui.util.UIUtils;
import com.aptana.ui.util.WorkbenchBrowserUtil;

/**
 * @author cwilliams
 */
public abstract class AbstractGithubHandler extends AbstractGitHandler
{

	/**
	 * Grabs the {@link IGithubRepository} for the selected repository. Forces user to login to github if not logged in.
	 * If user cancels login or some other error occurs we bail out and return null.
	 * 
	 * @return
	 * @throws ExecutionException
	 */
	protected IGithubRepository getGithubRepo() throws ExecutionException
	{
		final GitRepository repo = getSelectedRepository();
		if (repo == null)
		{
			throw new ExecutionException(Messages.CreatePullRequestHandler_NoRepoErr);
		}

		IGithubRepository ghRepo = null;
		while (ghRepo == null)
		{
			try
			{
				ghRepo = repo.getGithubRepo();
			}
			catch (CoreException ce)
			{
				if (ce.getStatus().getCode() == IGithubManager.GITHUB_LOGIN_CODE)
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
							userInfoControl.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(3, 1)
									.create());
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
					// we'll restart the loop and try again...
				}
				else
				{
					// some other error. Quit trying to get repo from github
					throw new ExecutionException(ce.getMessage(), ce);
				}
			}
		}

		return ghRepo;
	}

	@Override
	protected boolean calculateEnabled()
	{
		GitRepository repo = getSelectedRepository();
		if (repo == null)
		{
			return false;
		}

		return repo.hasGithubRemote();
	}

	public static void viewPullRequest(final IGithubPullRequest pr)
	{
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
				if (!StringUtil.isEmpty(prURL))
				{
					WorkbenchBrowserUtil.launchExternalBrowser(prURL);
				}
			}
		});
	}
}