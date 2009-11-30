package com.aptana.git.ui.actions;

import org.eclipse.jface.window.Window;

import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.internal.actions.CommitDialog;

public class CommitAction extends GitAction
{

	@Override
	public void run()
	{
		GitRepository theRepo = getSelectedRepository();
		CommitDialog dialog = new CommitDialog(getTargetPart().getSite().getShell(), theRepo);
		if (dialog.open() == Window.OK)
		{
			theRepo.index().commit(dialog.getCommitMessage());
		}
	}
}
