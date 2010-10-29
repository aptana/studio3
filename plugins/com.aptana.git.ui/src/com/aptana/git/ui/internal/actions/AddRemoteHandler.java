package com.aptana.git.ui.internal.actions;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;

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

		final String[] finalURL = new String[] { url };
		final String[] finalName = new String[] { name };
		final boolean[] finalTrack = new boolean[] { false };

		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable()
		{

			public void run()
			{
				AddRemoteDialog dialog = new AddRemoteDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getShell(), repo, finalName[0], finalURL[0]);
				if (dialog.open() == Window.OK)
				{
					finalName[0] = dialog.getValue().trim();
					finalURL[0] = dialog.getRemoteURL();
					finalTrack[0] = dialog.track();
				}
				else
				{
					finalName[0] = null;
					finalURL[0] = null;
					finalTrack[0] = false;
				}
			}
		});
		if (finalName[0] == null)
		{
			return null; // don't let command run!
		}
		if (finalTrack[0])
		{
			return new String[] { "remote", "add", "--track", repo.currentBranch(), finalName[0], finalURL[0] }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		return new String[] { "remote", "add", finalName[0], finalURL[0] }; //$NON-NLS-1$ //$NON-NLS-2$
	}

	protected void postLaunch(GitRepository repo)
	{
		// do nothing
	}

}
