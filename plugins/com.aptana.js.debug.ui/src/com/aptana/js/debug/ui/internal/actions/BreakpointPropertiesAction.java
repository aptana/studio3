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
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.PropertyDialogAction;

import com.aptana.js.debug.core.model.IJSLineBreakpoint;
import com.aptana.ui.util.UIUtils;

/**
 * @author Max Stepanov
 */
public class BreakpointPropertiesAction implements IObjectActionDelegate {
	private IJSLineBreakpoint breakpoint;

	/**
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction,
	 *      org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		PropertyDialogAction propertyAction = new PropertyDialogAction(new SameShellProvider(UIUtils.getActiveShell()),
				new ISelectionProvider() {
					public void addSelectionChangedListener(ISelectionChangedListener listener) {
					}

					public ISelection getSelection() {
						return new StructuredSelection(breakpoint);
					}

					public void removeSelectionChangedListener(ISelectionChangedListener listener) {
					}

					public void setSelection(ISelection selection) {
					}
				});
		propertyAction.run();
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		breakpoint = null;
		if (selection instanceof IStructuredSelection) {
			Object object = ((IStructuredSelection) selection).getFirstElement();
			if (object instanceof IJSLineBreakpoint) {
				breakpoint = (IJSLineBreakpoint) object;
			}
		}
	}
}
