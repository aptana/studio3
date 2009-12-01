package com.aptana.git.ui.internal.actions;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;

import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.actions.GitAction;
import com.aptana.git.ui.dialogs.CreateBranchDialog;

public class CreateBranchAction extends GitAction
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
		CreateBranchDialog dialog = new CreateBranchDialog(getTargetPart().getSite().getShell(), theRepo);
		if (dialog.open() == Window.OK)
		{
			String branchName = dialog.getValue().trim();
			boolean track = dialog.track();
			String startPoint = dialog.getStartPoint();
			if (theRepo.createBranch(branchName, track, startPoint))
			{
				// Do we want to always switch to the newly created branch?
				theRepo.switchBranch(branchName);
			}
		}
	}
}
