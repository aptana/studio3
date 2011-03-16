/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

import com.aptana.ide.syncing.ui.dialogs.SiteConnectionsEditorDialog;

/**
 * @author Ingo Muschenetz
 */
public class ConnectionManagerAction implements IObjectActionDelegate, IViewActionDelegate
{

	private IWorkbenchPart fActivePart;
	@SuppressWarnings("unused")
	private ISelection fSelection;

	public ConnectionManagerAction()
	{
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{
		fActivePart = targetPart;
	}

	public void run(IAction action)
	{
		SiteConnectionsEditorDialog dlg = new SiteConnectionsEditorDialog(fActivePart.getSite().getShell());
		dlg.open();
	}

	public void selectionChanged(IAction action, ISelection selection)
	{
		setSelection(selection);
	}

	public void init(IViewPart view)
	{
		fActivePart = view;
	}

	public void setSelection(ISelection selection)
	{
		fSelection = selection;
	}
}
