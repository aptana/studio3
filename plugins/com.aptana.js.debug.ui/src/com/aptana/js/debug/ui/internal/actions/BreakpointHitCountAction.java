/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.aptana.core.logging.IdeLog;
import com.aptana.debug.ui.DebugUiPlugin;
import com.aptana.js.debug.core.model.IJSLineBreakpoint;
import com.aptana.js.debug.ui.JSDebugUIPlugin;
import com.aptana.js.debug.ui.internal.dialogs.HitCountDialog;
import com.aptana.ui.util.UIUtils;

/**
 * @author Max Stepanov
 */
public class BreakpointHitCountAction implements IObjectActionDelegate {
	private IStructuredSelection selection;

	private IInputValidator inputValidator = new IInputValidator() {

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.IInputValidator#isValid(java.lang.String)
		 */
		public String isValid(String newText) {
			int value = -1;
			try {
				value = Integer.valueOf(newText.trim()).intValue();
			} catch (NumberFormatException e) {
				e.getCause();
			}
			if (value < 1) {
				return Messages.BreakpointHitCountAction_HitCountPositiveInteger;
			}
			return null;
		}
	};

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface. action.IAction,
	 * org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action .IAction,
	 * org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = null;
		if (selection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection) selection;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		if (selection != null && !selection.isEmpty()) {
			for (Object object : selection.toList()) {
				IJSLineBreakpoint breakpoint = (IJSLineBreakpoint) object;
				try {
					int oldValue = breakpoint.getHitCount();
					int newValue = showDialog(breakpoint);
					if (newValue != -1) {
						if (oldValue == 0 && newValue == 0) {
							break;
						}
						breakpoint.setHitCount(newValue);
					}
				} catch (CoreException ce) {
					DebugUiPlugin.errorDialog(Messages.BreakpointHitCountAction_ExceptionAttemptingToSetHitCount, ce);
				}
			}
		}
	}

	private int showDialog(IJSLineBreakpoint breakpoint) {
		int currentHitCount = 0;
		try {
			currentHitCount = breakpoint.getHitCount();
		} catch (CoreException e) {
			IdeLog.logError(JSDebugUIPlugin.getDefault(), e);
		}
		String initialValue = (currentHitCount > 0) ? Integer.toString(currentHitCount) : "1"; //$NON-NLS-1$;

		HitCountDialog dlg = new HitCountDialog(UIUtils.getActiveShell(),
				Messages.BreakpointHitCountAction_SetBreakpointHitCount,
				Messages.BreakpointHitCountAction_EnterNewHitCountForBreakpoint, initialValue, inputValidator);
		if (dlg.open() != Window.OK) {
			return -1;
		}
		if (dlg.isHitCountEnabled()) {
			return Integer.parseInt(dlg.getValue().trim());
		}
		return 0;
	}
}
