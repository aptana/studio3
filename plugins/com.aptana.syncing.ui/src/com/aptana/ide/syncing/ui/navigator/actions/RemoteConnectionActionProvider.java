/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.navigator.actions;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

import com.aptana.ide.syncing.ui.SyncingUIPlugin;
import com.aptana.ui.util.UIUtils;

public class RemoteConnectionActionProvider extends CommonActionProvider
{

	private static final String CONNECTION_ICON = "icons/full/obj16/connection.png"; //$NON-NLS-1$

	private RemoteConnectionManagerAction connectionManagerAction;

	private boolean isContributed;

	public RemoteConnectionActionProvider()
	{
	}

	@Override
	public void init(ICommonActionExtensionSite aSite)
	{
		super.init(aSite);

		connectionManagerAction = new RemoteConnectionManagerAction(getActivePart(aSite));
		connectionManagerAction.setImageDescriptor(SyncingUIPlugin.getImageDescriptor(CONNECTION_ICON));
	}

	@Override
	public void fillActionBars(IActionBars actionBars)
	{
		if (!isContributed)
		{
			fillMenu(actionBars.getMenuManager());
			actionBars.updateActionBars();
			isContributed = true;
		}
	}

	private void fillMenu(IMenuManager menuManager)
	{
		menuManager.add(connectionManagerAction);
	}

	private static IWorkbenchPart getActivePart(ICommonActionExtensionSite aSite)
	{
		ICommonViewerSite viewerSite = aSite.getViewSite();
		if (viewerSite instanceof ICommonViewerWorkbenchSite)
		{
			return ((ICommonViewerWorkbenchSite) viewerSite).getPart();
		}
		return UIUtils.getActivePage().getActivePart();
	}
}
