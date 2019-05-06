/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal.actions;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import com.aptana.js.debug.core.ILaunchConfigurationConstants;
import com.aptana.js.debug.core.model.IJSDebugTarget;

/**
 * @author Max Stepanov
 */
public class SuspendOnActionDelegate implements IViewActionDelegate {
	private IJSDebugTarget selectedTarget;

	/**
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view) {
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		if (selectedTarget != null) {
			boolean checked = action.isChecked();
			selectedTarget.setAttribute(getActionOption(action), Boolean.toString(checked));
		}
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		boolean enabled = false;
		boolean checked = false;
		selectedTarget = null;
		if (selection instanceof IStructuredSelection) {
			Object selectedObject = ((IStructuredSelection) selection).getFirstElement();
			if (selectedObject instanceof IAdaptable) {
				IDebugTarget target = (IDebugTarget) ((IAdaptable) selectedObject).getAdapter(IDebugTarget.class);
				if (target instanceof IJSDebugTarget && !((IDebugTarget) target).isDisconnected()) {
					enabled = true;
					selectedTarget = (IJSDebugTarget) target;
					String attribute = selectedTarget.getAttribute(getActionOption(action));
					checked = Boolean.valueOf(attribute).booleanValue();
				}
			}
		}
		action.setChecked(checked);
		action.setEnabled(enabled);
	}

	private String getActionOption(IAction action) {
		String id = action.getId();
		if ("com.aptana.js.debug.ui.actions.suspendOnExceptions".equals(id)) {
			return ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_ALL_EXCEPTIONS;
		}
		if ("com.aptana.js.debug.ui.actions.suspendOnErrors".equals(id)) {
			return ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_UNCAUGHT_EXCEPTIONS;
		}
		int index = id.lastIndexOf('.');
		if (index > 0) {
			id = id.substring(index + 1);
		}
		return id;
	}
}
