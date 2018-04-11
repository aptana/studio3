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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.handlers.HandlerUtil;

import com.aptana.ui.IPropertyDialog;
import com.aptana.ui.PropertyDialogsRegistry;
import com.aptana.ui.util.UIUtils;
import com.aptana.webserver.core.IServer;

/**
 * Add a new IServer
 * 
 * @author cwilliams
 */
public class EditServerHandler extends AbstractHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection.isEmpty() || !(selection instanceof IStructuredSelection))
		{
			return null;
		}

		IServer server = (IServer) ((IStructuredSelection) selection).getFirstElement();
		if (server != null)
		{
			editServerConfiguration(server);
		}
		return null;
	}

	static boolean editServerConfiguration(IServer server)
	{
		try
		{
			Dialog dlg = PropertyDialogsRegistry.getInstance().createPropertyDialog(server,
					new SameShellProvider(UIUtils.getActiveShell()));
			if (dlg != null)
			{
				if (dlg instanceof IPropertyDialog)
				{
					((IPropertyDialog) dlg).setPropertySource(server);
				}
				return dlg.open() == Window.OK;
			}
		}
		catch (CoreException e)
		{
			UIUtils.showErrorMessage("Failed to open server preferences dialog", e); //$NON-NLS-1$
		}
		return false;
	}

}
