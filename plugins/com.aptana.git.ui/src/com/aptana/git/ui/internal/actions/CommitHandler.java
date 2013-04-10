/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.actions;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;

import com.aptana.git.core.model.GitRepository;
import com.aptana.ui.util.UIUtils;

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
		CommitDialog dialog = new CommitDialog(getShell(), theRepo);
		if (dialog.open() == Window.OK)
		{
			IStatus status = theRepo.index().commit(dialog.getCommitMessage());
			if (!status.isOK())
			{
				// Open a dialog/toast letting user know commit didn't happen!
				int kind;
				switch (status.getSeverity())
				{
					case IStatus.ERROR:
						kind = MessageDialog.ERROR;
						break;

					default:
						kind = MessageDialog.WARNING;
						break;
				}
				UIUtils.showMessageDialogFromBgThread(kind, "Commit failed", status.getMessage(), null);
			}
		}
		return null;
	}

	@Override
	protected boolean calculateEnabled()
	{
		GitRepository repo = getSelectedRepository();
		if (repo == null)
		{
			return false;
		}
		return !repo.index().changedFiles().isEmpty() || repo.hasUnresolvedMergeConflicts();
	}
}
