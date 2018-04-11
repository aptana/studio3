/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.IDebugView;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;

import com.aptana.debug.ui.DebugUiPlugin;
import com.aptana.js.debug.core.model.IJSExceptionBreakpoint;
import com.aptana.js.debug.core.model.JSDebugModel;
import com.aptana.ui.util.UIUtils;

/**
 * @author Max Stepanov
 */
public class AddExceptionBreakpointDialog extends JSTypeSelectionDialog {
	/**
	 * AddExceptionBreakpointDialog
	 * 
	 * @param parent
	 */
	public AddExceptionBreakpointDialog(Shell parent) {
		super(parent);
		setFilter("*Error*"); //$NON-NLS-1$
	}

	/** TODO: addition exception handling properties (caught/uncaught) */

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {
		if (createBreakpoint()) {
			super.okPressed();
		}
	}

	private boolean createBreakpoint() {
		final Object[] selected = getSelectedElements();
		if (selected.length != 1) {
			return false;
		}

		new Job(Messages.AddExceptionBreakpointDialog_AddJavaScriptExceptionBreakpoint) {

			protected IStatus run(IProgressMonitor monitor) {
				try {
					IResource resource = null;
					if (resource == null) {
						resource = ResourcesPlugin.getWorkspace().getRoot();
					}
					IJSExceptionBreakpoint breakpoint = JSDebugModel.createExceptionBreakpoint(resource,
							(String) selected[0]);
					final List<IBreakpoint> list = new ArrayList<IBreakpoint>(1);
					list.add(breakpoint);
					Runnable r = new Runnable() {
						public void run() {
							IViewPart part = UIUtils.getActivePage().findView(IDebugUIConstants.ID_BREAKPOINT_VIEW);
							if (part instanceof IDebugView) {
								Viewer viewer = ((IDebugView) part).getViewer();
								if (viewer instanceof StructuredViewer) {
									StructuredViewer sv = (StructuredViewer) viewer;
									sv.setSelection(new StructuredSelection(list), true);
								}
							}
						}
					};
					DebugUiPlugin.getStandardDisplay().asyncExec(r);
					return Status.OK_STATUS;
				} catch (CoreException e) {
					updateStatus(e.getStatus());
					return Status.CANCEL_STATUS;
				}
			}

		}.schedule();
		return true;
	}
}
