package com.aptana.git.ui.internal.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;

import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.internal.dialogs.BranchDialog;

public class SwitchBranchAction extends GitAction
{

	@Override
	protected void execute(IAction action) throws InvocationTargetException, InterruptedException
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
		BranchDialog dialog = new BranchDialog(getTargetPart().getSite().getShell(), theRepo);
		if (dialog.open() == Window.OK)
		{
			theRepo.switchBranch(dialog.getBranch());
		}
	}

	@Override
	protected String[] getCommand()
	{
		return new String[] { "checkout" };
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

		GitRepository theRepo = repos.iterator().next();
		return theRepo.localBranches().size() > 1;
	}

}
