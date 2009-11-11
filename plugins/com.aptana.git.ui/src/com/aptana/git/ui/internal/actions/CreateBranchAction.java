package com.aptana.git.ui.internal.actions;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IResource;
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
		IResource[] resources = getSelectedResources();
		Set<GitRepository> repos = new HashSet<GitRepository>();
		for (IResource resource : resources)
		{
			GitRepository repo = GitRepository.getAttached(resource.getProject());
			if (repo != null)
				repos.add(repo);
		}
		if (repos.isEmpty())
		{
			MessageDialog.openError(getShell(), Messages.CommitAction_NoRepo_Title,
					Messages.CommitAction_NoRepo_Message);
			return;
		}
		if (repos.size() > 1)
		{
			MessageDialog.openError(getShell(), Messages.CommitAction_MultipleRepos_Title,
					Messages.CommitAction_MultipleRepos_Message);
			return;
		}
		GitRepository theRepo = repos.iterator().next();
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

	@Override
	protected String[] getCommand()
	{
		return new String[] { "branch" };
	}

	@Override
	public boolean isEnabled()
	{
		IResource[] resources = getSelectedResources();
		Set<GitRepository> repos = new HashSet<GitRepository>();
		for (IResource resource : resources)
		{
			GitRepository repo = GitRepository.getAttached(resource.getProject());
			if (repo != null)
				repos.add(repo);
		}
		if (repos.isEmpty())
			return false;

		if (repos.size() > 1)
			return false;

		return true;
	}

}
