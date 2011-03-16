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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.syncing.core.DefaultSiteConnection;
import com.aptana.ide.syncing.ui.editors.EditorUtils;

/**
 * Opens the editor for the default connection with the selected FTP site as the destination.
 * 
 * @author Michael Xia (mxia@aptana.com)
 */
public class OpenDefaultConnectionAction implements IObjectActionDelegate
{

	private IConnectionPoint fDestination;

	public OpenDefaultConnectionAction()
	{
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{
	}

	public void run(IAction action)
	{
		DefaultSiteConnection connection = DefaultSiteConnection.getInstance();
		connection.setDestination(fDestination);

		EditorUtils.openConnectionEditor(connection);
	}

	public void selectionChanged(IAction action, ISelection selection)
	{
		fDestination = null;
		if (selection instanceof IStructuredSelection)
		{
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof IConnectionPoint)
			{
				fDestination = (IConnectionPoint) element;
			}
		}
	}
}
