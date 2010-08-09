package com.aptana.ide.syncing.ui.navigator.actions;

import org.eclipse.ui.IWorkbenchPart;

import com.aptana.ide.syncing.ui.actions.UploadAction;

public class NavigatorUploadAction extends NavigatorBaseSyncAction
{

	public NavigatorUploadAction(IWorkbenchPart activePart)
	{
		super("Upload", activePart); //$NON-NLS-1$
	}

	@Override
	public void run()
	{
		UploadAction action = new UploadAction();
		action.setActivePart(null, getActivePart());
		action.setSelection(getStructuredSelection());
		action.run(null);
	}
}
