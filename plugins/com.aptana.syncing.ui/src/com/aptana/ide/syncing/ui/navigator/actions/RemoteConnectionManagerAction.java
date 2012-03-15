/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.navigator.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPart;

import com.aptana.ide.syncing.ui.dialogs.SiteConnectionsEditorDialog;

public class RemoteConnectionManagerAction extends Action
{

	private IWorkbenchPart activePart;

	public RemoteConnectionManagerAction(IWorkbenchPart activePart)
	{
		super(Messages.RemoteConnectionManagerAction_LBL_ConnectionManager);
		this.activePart = activePart;
	}

	@Override
	public void run()
	{
		SiteConnectionsEditorDialog dlg = new SiteConnectionsEditorDialog(activePart.getSite().getShell());
		dlg.open();
	}
}
