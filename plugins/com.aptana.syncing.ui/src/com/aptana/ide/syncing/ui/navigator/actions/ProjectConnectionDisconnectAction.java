/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.navigator.actions;

import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;

import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.syncing.ui.navigator.ProjectSiteConnection;

public class ProjectConnectionDisconnectAction implements IObjectActionDelegate
{

	protected ProjectSiteConnection projectConnection;
	protected IWorkbenchPart targetPart;

	public ProjectConnectionDisconnectAction()
	{
	}

	public void run(IAction action)
	{
		final IConnectionPoint connectionPoint = (IConnectionPoint) projectConnection
				.getAdapter(IConnectionPoint.class);
		if (connectionPoint == null || !connectionPoint.canDisconnect())
		{
			return;
		}
		Job job = new Job(MessageFormat.format(Messages.ProjectConnectionDisconnectAction_Disconnecting,
				connectionPoint.getName()))
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				if (targetPart instanceof CommonNavigator)
				{
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
					{
						public void run()
						{
							CommonViewer viewer = ((CommonNavigator) targetPart).getCommonViewer();
							viewer.collapseToLevel(projectConnection, AbstractTreeViewer.ALL_LEVELS);
						}
					});
				}
				if (connectionPoint.canDisconnect())
				{
					try
					{
						connectionPoint.disconnect(monitor);
					}
					catch (CoreException e)
					{
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

	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{
		this.targetPart = targetPart;
	}

	public void selectionChanged(IAction action, ISelection selection)
	{
		projectConnection = null;
		if (selection instanceof IStructuredSelection)
		{
			Object[] elements = ((IStructuredSelection) selection).toArray();
			for (Object element : elements)
			{
				if (element instanceof ProjectSiteConnection)
				{
					projectConnection = (ProjectSiteConnection) element;
					break;
				}
			}
		}
		action.setEnabled(projectConnection != null && projectConnection.canDisconnect());
	}
}
