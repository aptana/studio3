package com.aptana.git.ui.internal.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;

import com.aptana.git.core.model.GitRepository;

public class CommitAction extends GitAction
{

	private static final String COMMAND = "commit";

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
			MessageDialog.openError(getShell(), "No Git repository",
					"No Git repository is tied to the selected resources.");
			return;
		}
		if (repos.size() > 1)
		{
			MessageDialog.openError(getShell(), "More than one repository",
					"More than one repository is tied to the selected resources.");
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
	protected String getCommand()
	{
		return COMMAND;
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
