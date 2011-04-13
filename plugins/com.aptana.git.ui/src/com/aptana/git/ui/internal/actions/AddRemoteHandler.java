package com.aptana.git.ui.internal.actions;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.window.Window;

import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.dialogs.AddRemoteDialog;

public class AddRemoteHandler extends AbstractSimpleGitCommandHandler
{

	protected String[] getCommand()
	{
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
		url = MessageFormat.format("git://github.com/{0}/{1}.git", username, reponame); //$NON-NLS-1$

		AddRemoteDialog dialog = new AddRemoteDialog(getShell(), repo, name, url);
		if (dialog.open() == Window.OK)
		{
			name = dialog.getRemoteName().trim();
			url = dialog.getRemoteURL();
			if (dialog.track())
			{
				return new String[] { "remote", "add", "--track", repo.currentBranch(), name, url }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			return new String[] { "remote", "add", name, url }; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return null; // don't let command run!
	}

	protected void postLaunch(GitRepository repo)
	{
		// do nothing
	}

}
