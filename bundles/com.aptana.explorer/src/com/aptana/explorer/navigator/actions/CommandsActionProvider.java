/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.explorer.navigator.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.search.ui.IContextMenuConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.DeleteResourceAction;

import com.aptana.explorer.ExplorerPlugin;
import com.aptana.explorer.IExplorerUIConstants;
import com.aptana.explorer.internal.ui.Messages;

public class CommandsActionProvider extends ExplorerActionProvider
{

	public static final String ID = "com.aptana.explorer.navigator.actions.CommandsActions"; //$NON-NLS-1$

	@Override
	public String getActionId()
	{
		return ID;
	}

	@Override
	protected Image getImage()
	{
		return ExplorerPlugin.getImage(IExplorerUIConstants.GEAR_MENU_ICON);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ui.actions.DefaultNavigatorActionProvider#getHotImage()
	 */
	@Override
	protected Image getHotImage()
	{
		return ExplorerPlugin.getImage(IExplorerUIConstants.GEAR_HOT_MENU_ICON);
	}

	@Override
	protected String getMenuId()
	{
		return IExplorerUIConstants.GEAR_MENU_ID;
	}

	@Override
	protected String getToolTip()
	{
		return Messages.CommandsActionProvider_TTP_Commands;
	}

	protected void fillMenu(MenuManager menuManager)
	{
		IContributionItem item = menuManager.find(IContextMenuConstants.GROUP_PROPERTIES);
		if (item == null)
		{
			menuManager.add(new GroupMarker(IContextMenuConstants.GROUP_PROPERTIES));
		}
		// Stick Delete in Properties area
		menuManager.appendToGroup(IContextMenuConstants.GROUP_PROPERTIES, new ContributionItem()
		{

			@Override
			public void fill(Menu menu, int index)
			{
				final IProject selectedProject = getSelectedProject();
				MenuItem item = new MenuItem(menu, SWT.PUSH);
				item.setText(Messages.SingleProjectView_DeleteProjectMenuItem_LBL);
				item.addSelectionListener(new SelectionAdapter()
				{
					@Override
					public void widgetSelected(SelectionEvent e)
					{
						DeleteResourceAction action = new DeleteResourceAction(getPartSite());
						action.selectionChanged(new StructuredSelection(selectedProject));
						action.run();
					}
				});
				boolean enabled = (selectedProject != null && selectedProject.exists());
				ISharedImages images = PlatformUI.getWorkbench().getSharedImages();
				item.setImage(enabled ? images.getImage(ISharedImages.IMG_TOOL_DELETE) : images
						.getImage(ISharedImages.IMG_TOOL_DELETE_DISABLED));
				item.setEnabled(enabled);
			}

			@Override
			public boolean isDynamic()
			{
				return true;
			}
		});
	}
}
