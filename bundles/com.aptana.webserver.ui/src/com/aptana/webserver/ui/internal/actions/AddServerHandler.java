/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.webserver.ui.internal.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.dialogs.ListDialog;

import com.aptana.core.Identifiable;
import com.aptana.core.logging.IdeLog;
import com.aptana.ui.ImageAssociations;
import com.aptana.ui.util.UIUtils;
import com.aptana.webserver.core.IServer;
import com.aptana.webserver.core.IServerType;
import com.aptana.webserver.core.WebServerCorePlugin;
import com.aptana.webserver.ui.WebServerUIPlugin;
import com.aptana.webserver.ui.preferences.Messages;

/**
 * Add a new IServer
 * 
 * @author cwilliams
 */
public class AddServerHandler extends AbstractHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		ListDialog dlg = new ListDialog(UIUtils.getActiveShell());
		dlg.setContentProvider(ArrayContentProvider.getInstance());
		dlg.setLabelProvider(new LabelProvider()
		{
			@Override
			public Image getImage(Object element)
			{

				if (element instanceof Identifiable)
				{
					Identifiable identifiable = (Identifiable) element;
					String id = identifiable.getId();
					ImageRegistry imageRegistry = WebServerUIPlugin.getDefault().getImageRegistry();
					Image image = imageRegistry.get(id);
					if (image != null)
					{
						return image;
					}
					ImageDescriptor desc = ImageAssociations.getInstance().getImageDescriptor(id);
					if (desc != null)
					{
						imageRegistry.put(id, desc);
						return imageRegistry.get(id);
					}
				}
				return WebServerUIPlugin.getImage(WebServerUIPlugin.SERVER_ICON);
			}

			@Override
			public String getText(Object element)
			{
				if (element instanceof IServerType)
				{
					return ((IServerType) element).getName();
				}
				return super.getText(element);
			}
		});
		dlg.setInput(WebServerCorePlugin.getDefault().getServerManager().getServerTypes());
		dlg.setTitle(Messages.ServersPreferencePage_Title);
		if (dlg.open() != Window.OK)
		{
			return null;
		}
		Object[] result = dlg.getResult();
		if (result != null && result.length == 1)
		{
			String typeId = ((IServerType) result[0]).getId();
			createServer(typeId);
		}
		return null;
	}

	static boolean createServer(String serverTypeId)
	{
		try
		{
			IServer newServer = WebServerCorePlugin.getDefault().getServerManager().createServer(serverTypeId);
			if (newServer != null)
			{
				if (EditServerHandler.editServerConfiguration(newServer))
				{
					WebServerCorePlugin.getDefault().getServerManager().add(newServer);
					return true;
				}
			}
		}
		catch (CoreException e)
		{
			IdeLog.logError(WebServerUIPlugin.getDefault(), e);
		}
		return false;
	}

}
