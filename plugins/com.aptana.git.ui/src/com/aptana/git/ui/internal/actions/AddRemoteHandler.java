/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.actions;

import java.text.MessageFormat;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.window.Window;

import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.dialogs.AddRemoteDialog;

public class AddRemoteHandler extends AbstractGitHandler
{

	@Override
	protected Object doExecute(ExecutionEvent event) throws ExecutionException
	{
		GitRepository theRepo = getSelectedRepository();
		if (theRepo == null && getSelectedResources() != null && getSelectedResources().size() != 1)
		{
			openError(Messages.CommitAction_MultipleRepos_Title, Messages.CommitAction_MultipleRepos_Message);
			return null;
		}
		if (theRepo == null || getSelectedResources() == null || getSelectedResources().isEmpty())
		{
			openError(Messages.CommitAction_NoRepo_Title, Messages.CommitAction_NoRepo_Message);
			return null;
		}

		// Pop open a dialog like create branch!
		String name = "origin"; //$NON-NLS-1$
		String url = ""; //$NON-NLS-1$
		String username = System.getProperty("user.name"); //$NON-NLS-1$
		if (username == null || username.length() == 0)
		{
			username = "user"; //$NON-NLS-1$
		}
		String reponame = "repo"; //$NON-NLS-1$
		final GitRepository repo = getSelectedRepository();
		IPath wd = repo.workingDirectory();
		reponame = wd.lastSegment();
		if (reponame.endsWith(GitRepository.GIT_DIR))
		{
			reponame = reponame.substring(0, reponame.length() - 4);
		}
		url = MessageFormat.format("git@github.com:{0}/{1}.git", username, reponame); //$NON-NLS-1$

		AddRemoteDialog dialog = new AddRemoteDialog(getShell(), repo, name, url);
		if (dialog.open() == Window.OK)
		{
			name = dialog.getRemoteName().trim();
			url = dialog.getRemoteURL();
			theRepo.addRemote(name, url, dialog.track());
		}
		return null;
	}

}
