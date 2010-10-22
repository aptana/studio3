package com.aptana.git.ui.internal.actions;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;

import com.aptana.git.core.model.GitRepository;

public class CommitHandler extends AbstractGitHandler
{

	@Override
	protected Object doExecute(ExecutionEvent event) throws ExecutionException
	{
		GitRepository theRepo = getSelectedRepository();
		if (theRepo == null && getSelectedResources() != null && getSelectedResources().isEmpty())
		{
			openError(Messages.CommitAction_NoRepo_Title, Messages.CommitAction_NoRepo_Message);
			return null;
		}
		if (theRepo == null && getSelectedResources() != null && getSelectedResources().size() != 1)
		{
			openError(Messages.CommitAction_MultipleRepos_Title, Messages.CommitAction_MultipleRepos_Message);
			return null;
		}
		CommitDialog dialog = new CommitDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), theRepo);
		if (dialog.open() == Window.OK)
		{
			boolean success = theRepo.index().commit(dialog.getCommitMessage());
			if (!success)
			{
				// TODO Open an error dialog?
			}
		}
		return null;
	}

}
