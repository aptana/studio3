/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal.actions;

import java.net.URI;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.aptana.debug.ui.SourceDisplayUtil;
import com.aptana.js.debug.core.model.JSDebugModel;
import com.aptana.ui.dialogs.InputURLDialog;

/**
 * @author Max Stepanov
 */
public class OpenURLAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow fWindow;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	public void dispose() {
		fWindow = null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui. IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow window) {
		fWindow = window;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		InputURLDialog dlg = new InputURLDialog(fWindow.getShell(), Messages.OpenURLAction_Open_URL,
				Messages.OpenURLAction_Specify_URL_To_Open, "http://"); //$NON-NLS-1$
		if (dlg.open() == Window.OK) {
			SourceDisplayUtil.displaySource(JSDebugModel.createSourceLink(URI.create(dlg.getValue())),
					fWindow.getActivePage(), true);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action .IAction,
	 * org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
