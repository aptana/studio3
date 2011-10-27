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
 * Restart a server in a specific run mode. Mode is taken from current run mode of the server.
 * 
 * @author cwilliams
 */
public class RestartServerHandler extends AbstractHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection.isEmpty() || !(selection instanceof IStructuredSelection))
		{
			return null;
		}

		final IServer server = (IServer) ((IStructuredSelection) selection).getFirstElement();
		final String mode = server.getMode();
		if (server != null)
		{
			Job job = new Job(Messages.RestartServerHandler_JobName)
			{
				protected IStatus run(IProgressMonitor monitor)
				{
					return server.restart(mode, monitor);
				}

			};
			job.schedule();

		}
		return null;
	}

}
