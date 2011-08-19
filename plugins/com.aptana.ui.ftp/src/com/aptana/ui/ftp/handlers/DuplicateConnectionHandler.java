/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable unnecessaryExceptions

package com.aptana.ui.ftp.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.aptana.core.logging.IdeLog;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.IConnectionPointManager;
import com.aptana.ui.ftp.FTPUIPlugin;

public class DuplicateConnectionHandler extends AbstractHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (!selection.isEmpty() && selection instanceof IStructuredSelection)
		{
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof IConnectionPoint)
			{
				try
				{
					IConnectionPointManager manager = CoreIOPlugin.getConnectionPointManager();
					IConnectionPoint newConnection = manager.cloneConnectionPoint((IConnectionPoint) element);
					manager.addConnectionPoint(newConnection);
				}
				catch (CoreException e)
				{
					IdeLog.logError(FTPUIPlugin.getDefault(), "Failed to duplicate the connection", e); //$NON-NLS-1$
				}
			}
		}
		return null;
	}
}
