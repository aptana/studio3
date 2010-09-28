/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
