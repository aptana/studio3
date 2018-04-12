/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.navigator.actions;

import org.eclipse.ui.IWorkbenchPart;

import com.aptana.ide.syncing.ui.actions.UploadAction;

public class NavigatorUploadAction extends NavigatorBaseSyncAction
{

	public NavigatorUploadAction(IWorkbenchPart activePart)
	{
		super(Messages.NavigatorUploadAction_LBL_Upload, activePart);
	}

	@Override
	public void run()
	{
		UploadAction action = new UploadAction();
		action.setActivePart(null, getActivePart());
		action.setSelection(getStructuredSelection(), isSelectionFromSource());
		action.run(null);
	}
}
