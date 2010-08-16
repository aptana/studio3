package com.aptana.ide.syncing.ui.navigator.actions;

import org.eclipse.ui.IWorkbenchPart;

import com.aptana.ide.syncing.ui.actions.DownloadAction;

public class NavigatorDownloadAction extends NavigatorBaseSyncAction
{

	public NavigatorDownloadAction(IWorkbenchPart activePart)
	{
		super("Download", activePart); //$NON-NLS-1$
	}

	@Override
	public void run()
	{
		DownloadAction action = new DownloadAction();
		action.setActivePart(null, getActivePart());
		action.setSelection(getStructuredSelection(), isSelectionFromSource());
		action.run(null);
	}
}
