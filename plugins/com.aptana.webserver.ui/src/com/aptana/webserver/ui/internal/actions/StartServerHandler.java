/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.webserver.ui.internal.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.aptana.webserver.core.IServer;

/**
 * Start a server in a specific run mode. Run mode is given via "mode" parameter.
 * 
 * @author cwilliams
 */
public class StartServerHandler extends AbstractHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection == null || selection.isEmpty() || !(selection instanceof IStructuredSelection))
		{
			return null;
		}
		final String mode = event.getParameter("mode"); //$NON-NLS-1$

		final IServer server = (IServer) ((IStructuredSelection) selection).getFirstElement();
		if (server != null)
		{
			Job job = new Job(Messages.StartServerHandler_JobName)
			{
				protected IStatus run(IProgressMonitor monitor)
				{
					return server.start(mode, monitor);
				}

			};
			job.schedule();

		}
		return null;
	}

}
