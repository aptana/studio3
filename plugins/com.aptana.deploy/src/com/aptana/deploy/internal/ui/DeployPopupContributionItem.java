/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.internal.ui;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.menus.IMenuService;
import org.eclipse.ui.menus.IWorkbenchContribution;
import org.eclipse.ui.menus.MenuUtil;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.services.IServiceLocator;

import com.aptana.deploy.DeployPlugin;

public class DeployPopupContributionItem extends ContributionItem implements IWorkbenchContribution
{

	private IServiceLocator serviceLocator;

	public DeployPopupContributionItem()
	{
	}

	public DeployPopupContributionItem(String id)
	{
		super(id);
	}

	@Override
	public void fill(Menu menu, int index)
	{
		MenuManager menuManager = new MenuManager(Messages.DeployPopupContributionItem_Text,
				AbstractUIPlugin.imageDescriptorFromPlugin(DeployPlugin.getPluginIdentifier(),
						DeployPlugin.DEPLOY_MENU_ICON), DeployPlugin.DEPLOY_MENU_ID);
		IMenuService menuService = (IMenuService) serviceLocator.getService(IMenuService.class);
		menuService.populateContributionManager(menuManager, MenuUtil.menuUri(menuManager.getId()));
		menuManager.fill(menu, index);
	}

	public void initialize(IServiceLocator serviceLocator)
	{
		this.serviceLocator = serviceLocator;
	}
}
