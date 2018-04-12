/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Max Stepanov
 */
public abstract class ObjectActionDelegate implements IObjectActionDelegate {

	private IWorkbenchPart fPart;

	/**
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction,
	 *      org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		fPart = targetPart;
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * getCurrentSelection
	 * 
	 * @return IStructuredSelection
	 */
	protected IStructuredSelection getCurrentSelection() {
		if (fPart != null) {
			IWorkbenchPage page = fPart.getSite().getPage();
			if (page != null) {
				ISelection selection = page.getSelection();
				if (selection instanceof IStructuredSelection) {
					return (IStructuredSelection) selection;
				}
			}
		}
		return null;
	}

	/**
	 * refreshCurrentSelection
	 */
	protected void refreshCurrentSelection() {
		if (fPart != null) {
			IWorkbenchPage page = fPart.getSite().getPage();
			if (page != null) {
				ISelection selection = page.getSelection();
				if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
					fPart.getSite().getSelectionProvider().setSelection(selection);
				}
			}
		}
	}
}
