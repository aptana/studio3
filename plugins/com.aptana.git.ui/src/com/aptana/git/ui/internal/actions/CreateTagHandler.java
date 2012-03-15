package com.aptana.git.ui.internal.actions;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.window.Window;

import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.GitRevList;
import com.aptana.git.core.model.GitRevSpecifier;
import com.aptana.git.ui.dialogs.CreateTagDialog;

public class CreateTagHandler extends AbstractGitHandler
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

		GitRevList revList = new GitRevList(theRepo);
		revList.walkRevisionListWithSpecifier(new GitRevSpecifier("."), new NullProgressMonitor()); //$NON-NLS-1$

		CreateTagDialog dialog = new CreateTagDialog(getShell(), theRepo, revList.getCommits());
		if (dialog.open() == Window.OK)
		{
			String tagName = dialog.getTagName().trim();
			String message = dialog.getMessage();
			String startPoint = dialog.getStartPoint();
			theRepo.createTag(tagName, message, startPoint);
		}
		return null;
	}

}