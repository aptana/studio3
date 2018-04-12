/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.navigator.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

import com.aptana.ide.syncing.core.SiteConnectionUtils;
import com.aptana.ide.syncing.ui.SyncingUIPlugin;

public class SyncingActionProvider extends CommonActionProvider
{

	private static final String SYNC_IMAGE = "icons/full/elcl16/arrow_up_down.png"; //$NON-NLS-1$
	private static final String UPLOAD_IMAGE = "icons/full/elcl16/arrow_up.png"; //$NON-NLS-1$
	private static final String DOWNLOAD_IMAGE = "icons/full/elcl16/arrow_down.png"; //$NON-NLS-1$

	private static final String GROUP_SYNCING = "syncing"; //$NON-NLS-1$

	private NavigatorSynchronizeAction fSynchronizeAction;
	private NavigatorUploadAction fUploadAction;
	private NavigatorDownloadAction fDownloadAction;

	private boolean isToolbarFilled;
	private List<IContributionItem> hiddenItems;

	public SyncingActionProvider()
	{
		hiddenItems = new ArrayList<IContributionItem>();
	}

	@Override
	public void dispose()
	{
		hiddenItems.clear();
		super.dispose();
	}

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
		boolean hasSyncConnection = false;
		IStructuredSelection selection = getSelection();
		if (!selection.isEmpty())
		{
			Object element = selection.getFirstElement();
			if (element instanceof IAdaptable)
			{
				hasSyncConnection = hasSyncConnection((IAdaptable) element);
			}
		}
		// fillActionBars() is called each time the selection changes, so adds a check to only add the toolbar items
		// once
		IToolBarManager toolbar = actionBars.getToolBarManager();
		if (!isToolbarFilled)
		{
			if (hasSyncConnection)
			{
				fillToolBar(toolbar);
				actionBars.updateActionBars();
				isToolbarFilled = true;
			}
		}
		else if (updateToolbar(toolbar, hasSyncConnection))
		{
			actionBars.updateActionBars();
		}
		updateSelection();
	}

	protected void fillToolBar(IToolBarManager toolBar)
	{
		toolBar.add(new GroupMarker(GROUP_SYNCING));
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

	private boolean updateToolbar(IToolBarManager toolbar, boolean hasSyncConnection)
	{
		boolean updated = false;
		IContributionItem[] items = toolbar.getItems();
		if (hasSyncConnection)
		{
			// adds the syncing items back, but only needs to do it once until after the next time hasSyncConnection is
			// false
			for (IContributionItem hiddenItem : hiddenItems)
			{
				toolbar.appendToGroup(GROUP_SYNCING, hiddenItem);
			}
			updated = hiddenItems.size() > 0;
			hiddenItems.clear();
		}
		else
		{
			// removes the syncing items
			for (IContributionItem item : items)
			{
				if (item instanceof ActionContributionItem)
				{
					if (((ActionContributionItem) item).getAction() instanceof NavigatorBaseSyncAction)
					{
						toolbar.remove(item);
						hiddenItems.add(item);
						updated = true;
					}
				}
			}
		}
		return updated;
	}

	private static boolean hasSyncConnection(IAdaptable element)
	{
		return (SiteConnectionUtils.findSitesForSource(element).length > 0)
				|| (SiteConnectionUtils.findSitesWithDestination(element).length > 0);
	}
}
