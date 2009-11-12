package com.aptana.git.ui.internal.actions;

import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.actions.MenuAction;
import com.aptana.git.ui.internal.dialogs.BranchDialog;

public class SwitchBranchAction extends MenuAction
{

	/**
	 * Fills the fly-out menu
	 */
	protected void fillMenu(Menu menu)
	{
		IResource resource = getSelectedResource();
		if (resource == null)
			return;

		final GitRepository repo = GitRepository.getAttached(resource.getProject());
		if (repo == null)
			return;

		Set<String> localBranches = repo.localBranches();
		int index = 0;
		for (final String branchName : localBranches)
		{
			MenuItem menuItem = new MenuItem(menu, SWT.PUSH, index++);
			menuItem.setText(branchName);
			menuItem.setEnabled(!branchName.equals(repo.currentBranch()));
			menuItem.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					// what to do when menu is subsequently selected.
					repo.switchBranch(branchName);
				}
			});
		}
	}

	public void run(IAction action)
	{
		// Called when keybinding is used
		IResource resource = getSelectedResource();
		if (resource == null)
			return;

		final GitRepository repo = GitRepository.getAttached(resource.getProject());
		if (repo == null)
			return;

		BranchDialog dialog = new BranchDialog(Display.getDefault().getActiveShell(), repo, true, false);
		if (dialog.open() == Window.OK)
			repo.switchBranch(dialog.getBranch());
	}

}
