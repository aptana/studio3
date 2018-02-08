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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.aptana.ui.util.UIUtils;
import com.aptana.webserver.core.IServer;
import com.aptana.webserver.core.WebServerCorePlugin;
import com.aptana.webserver.ui.preferences.Messages;

/**
 * Delete an IServer
 * 
 * @author cwilliams
 */
public class DeleteServerHandler extends AbstractHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{

		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection.isEmpty() || !(selection instanceof IStructuredSelection))
		{
			return null;
		}

		IServer server = (IServer) ((IStructuredSelection) selection).getFirstElement();
		if (server != null
				&& MessageDialog.openQuestion(UIUtils.getActiveShell(),
						Messages.ServersPreferencePage_DeletePrompt_Title,
						Messages.ServersPreferencePage_DeletePrompt_Message))
		{
			// TODO We should probably make sure server gets stopped before we remove it!
			WebServerCorePlugin.getDefault().getServerManager().remove(server);
		}
		return null;
	}
}
