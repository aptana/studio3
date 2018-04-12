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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.window.Window;

import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.dialogs.CreateBranchDialog;

public class CreateBranchHandler extends AbstractGitHandler
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
		CreateBranchDialog dialog = new CreateBranchDialog(getShell(), theRepo);
		if (dialog.open() == Window.OK)
		{
			String branchName = dialog.getValue().trim();
			boolean track = dialog.track();
			String startPoint = dialog.getStartPoint();
			if (theRepo.createBranch(branchName, track, startPoint))
			{
				// Do we want to always switch to the newly created branch?
				theRepo.switchBranch(branchName, new NullProgressMonitor());
			}
		}
		return null;
	}

}
