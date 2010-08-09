package com.aptana.ide.syncing.ui.navigator.actions;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

import com.aptana.ide.syncing.ui.SyncingUIPlugin;

public class SyncingActionProvider extends CommonActionProvider
{

	private static final String SYNC_IMAGE = "icons/full/obj16/sync_both.gif"; //$NON-NLS-1$
	private static final String UPLOAD_IMAGE = "icons/full/obj16/put.gif"; //$NON-NLS-1$
	private static final String DOWNLOAD_IMAGE = "icons/full/obj16/get.gif"; //$NON-NLS-1$

	private NavigatorSynchronizeAction fSynchronizeAction;
	private NavigatorUploadAction fUploadAction;
	private NavigatorDownloadAction fDownloadAction;

	private boolean isToolbarFilled;

	@Override
	public void init(ICommonActionExtensionSite aSite)
	{
		super.init(aSite);

		IWorkbenchPart activePart;
		ICommonViewerSite viewerSite = aSite.getViewSite();
		if (viewerSite instanceof ICommonViewerWorkbenchSite)
		{
			activePart = ((ICommonViewerWorkbenchSite) viewerSite).getPart();
		}
		else
		{
			activePart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		}
		fSynchronizeAction = new NavigatorSynchronizeAction(activePart);
		fSynchronizeAction.setImageDescriptor(SyncingUIPlugin.getImageDescriptor(SYNC_IMAGE));
		fUploadAction = new NavigatorUploadAction(activePart);
		fUploadAction.setImageDescriptor(SyncingUIPlugin.getImageDescriptor(UPLOAD_IMAGE));
		fDownloadAction = new NavigatorDownloadAction(activePart);
		fDownloadAction.setImageDescriptor(SyncingUIPlugin.getImageDescriptor(DOWNLOAD_IMAGE));
	}

	@Override
	public void fillActionBars(IActionBars actionBars)
	{
		// fillActionBars() is called each time the selection changes, so adds a check to only add the toolbar items
		// once
		if (!isToolbarFilled)
		{
			fillToolBar(actionBars.getToolBarManager());
			actionBars.updateActionBars();
			isToolbarFilled = true;
		}
		updateSelection();
	}

	protected void fillToolBar(IToolBarManager toolBar)
	{
		toolBar.add(new GroupMarker("syncing")); //$NON-NLS-1$
		toolBar.add(fSynchronizeAction);
		toolBar.add(fUploadAction);
		toolBar.add(fDownloadAction);
	}

	private IStructuredSelection getSelection()
	{
		// could safely cast
		return (IStructuredSelection) getContext().getSelection();
	}

	private void updateSelection()
	{
		IStructuredSelection selection = getSelection();
		fSynchronizeAction.selectionChanged(selection);
		fUploadAction.selectionChanged(selection);
		fDownloadAction.selectionChanged(selection);
	}
}
