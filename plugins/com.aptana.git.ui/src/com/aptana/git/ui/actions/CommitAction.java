package com.aptana.git.ui.actions;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IResource;
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
		CommitDialog dialog = new CommitDialog(getTargetPart().getSite().getShell(), theRepo);
		if (dialog.open() == Window.OK)
		{
			theRepo.index().commit(dialog.getCommitMessage());
		}
	}

	@Override
	public boolean isEnabled()
	{
		IResource[] resources = getSelectedResources();
		for (IResource resource : resources)
		{
			if (resource == null)
				return false;
			GitRepository repo = GitRepository.getAttached(resource.getProject());
			if (repo == null)
				return false;
			// TODO check that repo actually has changed files? Probably not, since we want to allow amending
		}
		return true;
	}
}
