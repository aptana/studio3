/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.ui.io.actions;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;

import com.aptana.core.logging.IdeLog;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.ui.io.IOUIPlugin;
import com.aptana.ui.util.UIUtils;

/**
 * @author Max Stepanov
 */
public class DeleteConnectionAction extends ConnectionActionDelegate
{

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action)
	{
		IConnectionPoint[] connections = getSelectedConnectionPoints();
		if (connections.length == 0)
		{
			return;
		}
		final List<IConnectionPoint> connectionsToDelete = new ArrayList<IConnectionPoint>();
		for (IConnectionPoint connection : connections)
		{
			if (MessageDialog.openConfirm(UIUtils.getActiveShell(), Messages.DeleteConnectionAction_Confirm_Title,
					MessageFormat.format(Messages.DeleteConnectionAction_Confirm_Message, connection)))
			{
				connectionsToDelete.add(connection);
			}
		}

		Job job = new Job(Messages.DeleteConnectionAction_DeletingConnections)
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				for (IConnectionPoint connection : connectionsToDelete)
				{
					monitor.subTask(MessageFormat.format(Messages.DeleteConnectionAction_Deleting, connection));
					CoreIOPlugin.getConnectionPointManager().removeConnectionPoint(connection);
					if (connection.canDisconnect())
					{
						try
						{
							connection.disconnect(monitor);
						}
						catch (CoreException e)
						{
							IdeLog.logError(IOUIPlugin.getDefault(),
									Messages.DeleteConnectionAction_FailedToDisconnect, e);
						}
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
