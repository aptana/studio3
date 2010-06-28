package com.aptana.git.ui.actions;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;

import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.internal.actions.CommitDialog;
import com.aptana.git.ui.internal.actions.Messages;

public class CommitAction extends GitAction
{

	@Override
	public void run()
	{
		GitRepository theRepo = getSelectedRepository();
		if (theRepo == null && getSelectedResources() != null && getSelectedResources().length == 0)
		{
			MessageDialog.openError(getShell(), Messages.CommitAction_NoRepo_Title,
					Messages.CommitAction_NoRepo_Message);
			return;
		}
		if (theRepo == null && getSelectedResources() != null && getSelectedResources().length != 1)
		{
			MessageDialog.openError(getShell(), Messages.CommitAction_MultipleRepos_Title,
					Messages.CommitAction_MultipleRepos_Message);
			return;
		}
		CommitDialog dialog = new CommitDialog(getTargetPart().getSite().getShell(), theRepo);
		if (dialog.open() == Window.OK)
		{
			boolean success = theRepo.index().commit(dialog.getCommitMessage());
			if (!success)
			{
				// TODO Open an error dialog?
			}
		}
	}
}
