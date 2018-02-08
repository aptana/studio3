/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.navigator.actions;

import org.eclipse.ui.IWorkbenchPart;

import com.aptana.ide.syncing.ui.actions.SynchronizeFilesAction;

public class NavigatorSynchronizeAction extends NavigatorBaseSyncAction
{

	public NavigatorSynchronizeAction(IWorkbenchPart activePart)
	{
		super(Messages.NavigatorSynchronizeAction_LBL_Synchronize, activePart);
	}

	@Override
	public void run()
	{
		SynchronizeFilesAction action = new SynchronizeFilesAction();
		action.setActivePart(null, getActivePart());
		action.setSelection(getStructuredSelection(), isSelectionFromSource());
		action.run(null);
	}
}
