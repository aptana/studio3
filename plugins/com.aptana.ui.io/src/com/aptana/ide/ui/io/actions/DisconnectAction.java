/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.actions;

import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;

/**
 * @author Max Stepanov
 *
 */
public class DisconnectAction extends ConnectionActionDelegate {

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		if (connectionPoint == null || !connectionPoint.canDisconnect()) {
			return;
		}
		Job job = new Job(MessageFormat.format(Messages.DisconnectAction_Disconnecting, connectionPoint.getName())) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				if (targetPart instanceof CommonNavigator) {
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
						public void run() {
							CommonViewer viewer = ((CommonNavigator) targetPart).getCommonViewer();
							viewer.collapseToLevel(connectionPoint, AbstractTreeViewer.ALL_LEVELS);
						}
					});
				}
				if (connectionPoint.canDisconnect()) {
					try {
						connectionPoint.disconnect(monitor);
					} catch (CoreException e) {
						return e.getStatus();
					}
				}
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.setPriority(Job.LONG);
		job.setRule((ISchedulingRule) connectionPoint.getAdapter(ISchedulingRule.class));
		job.schedule();
	}
}
