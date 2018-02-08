/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ui.ftp.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ui.IPropertyDialog;
import com.aptana.ui.ftp.internal.FTPPropertyDialogProvider;

/**
 * @author Max Stepanov
 */
public class NewFTPConnectionAction implements IObjectActionDelegate {

	private static final String DEFAULT_TYPE = "ftp"; //$NON-NLS-1$

	private IWorkbenchPart targetPart;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction,
	 * org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		Dialog dlg = new FTPPropertyDialogProvider().createPropertyDialog(targetPart.getSite());
		if (dlg instanceof IPropertyDialog) {
			String typeId;
			if (action == null) {
				typeId = DEFAULT_TYPE;
			}
			else {
				typeId = action.getId();
				int index = typeId.lastIndexOf('.');
				if (index >= 0 && index + 1 < typeId.length()) {
					typeId = typeId.substring(index + 1);
				}
			}
			((IPropertyDialog) dlg).setPropertySource(CoreIOPlugin.getConnectionPointManager().getType(typeId));
		}
		dlg.open();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 * org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}
}
