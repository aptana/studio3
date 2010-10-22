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
		String name = "origin";
		String url = "";
		String username = System.getProperty("user.name"); //$NON-NLS-1$
		if (username == null || username.length() == 0)
			username = "user"; //$NON-NLS-1$
		String reponame = "repo";
		IPath wd = getSelectedRepository().workingDirectory();
		reponame = wd.lastSegment();
		if (reponame.endsWith(".git"))
			reponame = reponame.substring(0, reponame.length() - 4);
		url = MessageFormat.format("git://github.com/{0}/{1}.git", username, reponame); //$NON-NLS-1$

		final String[] finalURL = new String[] { url };
		final String[] finalName = new String[] { name };

		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable()
		{

			public void run()
			{
				AddRemoteDialog dialog = new AddRemoteDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getShell(), finalName[0], finalURL[0]);
				if (dialog.open() == Window.OK)
				{
					finalName[0] = dialog.getValue().trim();
					finalURL[0] = dialog.getRemoteURL();
				}
				else
				{
					finalName[0] = null;
					finalURL[0] = null;
				}
			}
		});
		if (finalName[0] == null)
		{
			return null; // don't let command run!
		}
		return new String[] { "remote", "add", finalName[0], finalURL[0] };
	}

	protected void postLaunch(GitRepository repo)
	{
		// do nothing
	}

}
