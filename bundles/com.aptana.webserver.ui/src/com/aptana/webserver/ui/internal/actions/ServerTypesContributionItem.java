/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.webserver.ui.internal.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.aptana.ui.ImageAssociations;
import com.aptana.webserver.core.IServerManager;
import com.aptana.webserver.core.IServerType;
import com.aptana.webserver.core.WebServerCorePlugin;
import com.aptana.webserver.ui.WebServerUIPlugin;

/**
 * Generates the Add server/New Server dropdown menu with the list of registered server types.
 * 
 * @author cwilliams
 */
public class ServerTypesContributionItem extends ContributionItem
{

	public ServerTypesContributionItem()
	{
	}

	public ServerTypesContributionItem(String id)
	{
		super(id);
	}

	@Override
	public void fill(Menu menu, int index)
	{
		IServerManager manager = WebServerCorePlugin.getDefault().getServerManager();
		List<IServerType> serverTypes = new ArrayList<IServerType>(manager.getServerTypes());
		// sort ignoring case
		Collections.sort(serverTypes, new Comparator<IServerType>()
		{
			public int compare(IServerType o1, IServerType o2)
			{
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		});
		for (IServerType type : serverTypes)
		{
			IContributionItem item = new NewServerContributionItem(type);
			item.fill(menu, menu.getItemCount());
		}
	}

	private class NewServerContributionItem extends ContributionItem
	{
		private IServerType serverType;

		NewServerContributionItem(IServerType serverType)
		{
			this.serverType = serverType;
		}

		@Override
		public void fill(Menu menu, int index)
		{
			MenuItem menuItem = new MenuItem(menu, SWT.PUSH, index);
			menuItem.setText(serverType.getName());
			menuItem.setImage(getImage(serverType));
			menuItem.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					AddServerHandler.createServer(serverType.getId());
				}
			});
		}

		private Image getImage(IServerType serverType)
		{
			String id = serverType.getId();
			Image img = WebServerUIPlugin.getDefault().getImageRegistry().get(id);
			if (img != null)
			{
				return img;
			}

			ImageDescriptor desc = ImageAssociations.getInstance().getImageDescriptor(id);
			if (desc != null)
			{
				WebServerUIPlugin.getDefault().getImageRegistry().put(id, desc);
				return WebServerUIPlugin.getDefault().getImageRegistry().get(id);
			}
			return WebServerUIPlugin.getImage(WebServerUIPlugin.SERVER_ICON);
		}
	}

}
