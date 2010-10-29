/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
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

	private static final String SYNC_IMAGE = "icons/full/elcl16/arrow_up_down.png"; //$NON-NLS-1$
	private static final String UPLOAD_IMAGE = "icons/full/elcl16/arrow_up.png"; //$NON-NLS-1$
	private static final String DOWNLOAD_IMAGE = "icons/full/elcl16/arrow_down.png"; //$NON-NLS-1$

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
