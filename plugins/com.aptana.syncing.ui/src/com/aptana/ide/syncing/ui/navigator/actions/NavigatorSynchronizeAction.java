package com.aptana.ide.syncing.ui.navigator.actions;

import org.eclipse.ui.IWorkbenchPart;

import com.aptana.ide.syncing.ui.actions.SynchronizeFilesAction;

public class NavigatorSynchronizeAction extends NavigatorBaseSyncAction
{

	public NavigatorSynchronizeAction(IWorkbenchPart activePart)
	{
		super("Synchronize", activePart); //$NON-NLS-1$
	}

	@Override
	public void run()
	{
		SynchronizeFilesAction action = new SynchronizeFilesAction();
		action.setActivePart(null, getActivePart());
		action.setSelection(getStructuredSelection());
		action.run(null);
	}
}
