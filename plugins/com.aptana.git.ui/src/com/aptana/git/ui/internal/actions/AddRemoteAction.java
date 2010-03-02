package com.aptana.git.ui.internal.actions;

import java.text.MessageFormat;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.window.Window;

import com.aptana.git.ui.actions.SimpleGitCommandAction;
import com.aptana.git.ui.dialogs.AddRemoteDialog;

public class AddRemoteAction extends SimpleGitCommandAction
{

	@SuppressWarnings("nls")
	@Override
	protected String[] getCommand()
	{
		// Pop open a dialog like create branch!
		String name = "origin";
		String url = "";
		String username = System.getProperty("user.name"); //$NON-NLS-1$
		if (username == null || username.length() == 0)
			username = "user"; //$NON-NLS-1$
		String reponame = "repo";
		String wd = getSelectedRepository().workingDirectory();
		reponame = new Path(wd).lastSegment();
		if (reponame.endsWith(".git"))
			reponame = reponame.substring(0, reponame.length() - 4);
		url = MessageFormat.format("git://github.com/{0}/{1}.git", username, reponame); //$NON-NLS-1$

		AddRemoteDialog dialog = new AddRemoteDialog(getTargetPart().getSite().getShell(), name, url);
		if (dialog.open() == Window.OK)
		{
			name = dialog.getValue().trim();
			url = dialog.getRemoteURL();
		}
		else
		{
			// FIXME Bail out!
		}
		return new String[] { "remote", "add", name, url };
	}

	@Override
	protected void postLaunch()
	{
		// do nothing
	}

}
