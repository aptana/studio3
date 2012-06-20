/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.engineyard.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.aptana.deploy.ui.DeployContributionItem;
import com.aptana.scripting.model.CommandElement;

public class EngineYardContributionItem extends DeployContributionItem
{

	private static final String BUNDLE_ENGINE_YARD = "Engine Yard"; //$NON-NLS-1$

	public EngineYardContributionItem()
	{
	}

	public EngineYardContributionItem(String id)
	{
		super(id);
	}

	@Override
	public void fill(Menu menu, int index)
	{
		// open ssh session
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText(Messages.EngineYardContributionItem_OpenSSHSubmenuLabel);
		item.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				CommandElement command = getBundleCommand(BUNDLE_ENGINE_YARD, "Open SSH Session"); //$NON-NLS-1$
				command.execute();
			}
		});

		// Deployment Submenu
		MenuItem deploymentMenuItem = new MenuItem(menu, SWT.CASCADE);
		deploymentMenuItem.setText(Messages.EngineYardContributionItem_DeploymentSubmenuLabel);
		Menu deploymentSubMenu = new Menu(menu);

		createDeploySubMenuItem(deploymentSubMenu, "List Environments", BUNDLE_ENGINE_YARD); //$NON-NLS-1$
		createDeploySubMenuItem(deploymentSubMenu, "Retrieve Logs", BUNDLE_ENGINE_YARD); //$NON-NLS-1$
		createDeploySubMenuItem(deploymentSubMenu, "Rebuild Environment", BUNDLE_ENGINE_YARD); //$NON-NLS-1$
		createDeploySubMenuItem(deploymentSubMenu, "Rollback App", BUNDLE_ENGINE_YARD); //$NON-NLS-1$
		deploymentMenuItem.setMenu(deploymentSubMenu);

		// Recipes Submenu
		MenuItem recipesMenuItem = new MenuItem(menu, SWT.CASCADE);
		recipesMenuItem.setText(Messages.EngineYardContributionItem_RecipesSubmenuLabel);
		Menu recipesSubMenu = new Menu(menu);

		createDeploySubMenuItem(recipesSubMenu, "Apply Recipes", BUNDLE_ENGINE_YARD); //$NON-NLS-1$
		createDeploySubMenuItem(recipesSubMenu, "Upload Recipes", BUNDLE_ENGINE_YARD); //$NON-NLS-1$
		createDeploySubMenuItem(recipesSubMenu, "Download Recipes", BUNDLE_ENGINE_YARD); //$NON-NLS-1$
		recipesMenuItem.setMenu(recipesSubMenu);

		// Maintenance Submenu
		MenuItem maintenanceMenuItem = new MenuItem(menu, SWT.CASCADE);
		maintenanceMenuItem.setText(Messages.EngineYardContributionItem_MaintenanceSubmenuLabel);
		Menu maintenanceSubMenu = new Menu(menu);

		createDeploySubMenuItem(maintenanceSubMenu, "Turn Maintenance On", BUNDLE_ENGINE_YARD); //$NON-NLS-1$
		createDeploySubMenuItem(maintenanceSubMenu, "Turn Maintenance Off", BUNDLE_ENGINE_YARD); //$NON-NLS-1$
		maintenanceMenuItem.setMenu(maintenanceSubMenu);
	}
}
