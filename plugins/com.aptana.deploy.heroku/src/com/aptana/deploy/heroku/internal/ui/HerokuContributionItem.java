/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.heroku.internal.ui;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.aptana.core.ShellExecutable;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ExecutableUtil;
import com.aptana.core.util.ProcessUtil;
import com.aptana.deploy.heroku.HerokuPlugin;
import com.aptana.deploy.ui.DeployContributionItem;
import com.aptana.ui.util.UIUtils;

public class HerokuContributionItem extends DeployContributionItem
{

	private static final String BUNDLE_HEROKU = "Heroku"; //$NON-NLS-1$

	public HerokuContributionItem()
	{
	}

	public HerokuContributionItem(String id)
	{
		super(id);
	}

	@Override
	public void fill(Menu menu, int index)
	{
		IResource selectedResource = UIUtils.getSelectedResource();
		if (selectedResource == null)
		{
			return;
		}

		final IProject selectedProject = selectedResource.getProject();
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText(Messages.HerokuContributionItem_OpenBrowserItem);
		item.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// run heroku info
				IPath workingDir = selectedProject.getLocation();
				Map<String, String> env = null;
				if (!Platform.OS_WIN32.equals(Platform.getOS()))
				{
					env = ShellExecutable.getEnvironment(workingDir);
				}
				IPath herokuPath = ExecutableUtil.find("heroku", true, null); //$NON-NLS-1$
				String output = ProcessUtil.outputForCommand(herokuPath.toOSString(), workingDir, env, "info"); //$NON-NLS-1$

				try
				{
					// extract url from heroku info
					if (output != null && output.contains("Web URL:")) //$NON-NLS-1$
					{
						String URL = output.split("Web URL:")[1].split("\n")[0].replace(" ", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

						// Determine which OS and open url
						if (Platform.OS_MACOSX.equals(Platform.getOS()))
						{
							ProcessUtil.run("open", null, (Map<String, String>) null, URL); //$NON-NLS-1$
						}
						else if (Platform.OS_WIN32.equals(Platform.getOS()))
						{
							ProcessUtil.run("cmd", null, (Map<String, String>) null, "/c", "start " + URL); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						}
						else
						{
							ProcessUtil.run("x-www-browser", null, (Map<String, String>) null, URL); //$NON-NLS-1$
						}
					}
				}
				catch (Exception e1)
				{
					IdeLog.logError(HerokuPlugin.getDefault(), e1);
				}
			}
		});

		// Sharing Submenu
		MenuItem sharingMenuItem = new MenuItem(menu, SWT.CASCADE);
		sharingMenuItem.setText(Messages.HerokuContributionItem_SharingSubmenuLabel);
		Menu sharingSubMenu = new Menu(menu);

		createDeploySubMenuItem(sharingSubMenu, "Add Collaborator", BUNDLE_HEROKU); //$NON-NLS-1$
		createDeploySubMenuItem(sharingSubMenu, "Remove Collaborator", BUNDLE_HEROKU); //$NON-NLS-1$
		sharingMenuItem.setMenu(sharingSubMenu);

		// Database
		MenuItem databaseMenuItem = new MenuItem(menu, SWT.CASCADE);
		databaseMenuItem.setText(Messages.HerokuContributionItem_DatabaseSubmenuLabel);
		Menu databaseSubMenu = new Menu(menu);

		createDeploySubMenuItem(databaseSubMenu, "Rake db:migrate on Heroku", BUNDLE_HEROKU); //$NON-NLS-1$
		createDeploySubMenuItem(databaseSubMenu, "Push Local Database to Heroku", BUNDLE_HEROKU); //$NON-NLS-1$
		createDeploySubMenuItem(databaseSubMenu, "Pull Remote Database from Heroku", BUNDLE_HEROKU); //$NON-NLS-1$

		databaseMenuItem.setMenu(databaseSubMenu);

		// Maintenance
		MenuItem maintenanceMenuItem = new MenuItem(menu, SWT.CASCADE);
		maintenanceMenuItem.setText(Messages.HerokuContributionItem_MaintenanceSubmenuLabel);
		Menu maintanenceSubMenu = new Menu(menu);

		createDeploySubMenuItem(maintanenceSubMenu, "Turn Maintence On", BUNDLE_HEROKU); //$NON-NLS-1$
		createDeploySubMenuItem(maintanenceSubMenu, "Turn Maintence Off", BUNDLE_HEROKU); //$NON-NLS-1$

		maintenanceMenuItem.setMenu(maintanenceSubMenu);

		// Remote
		MenuItem remoteMenuItem = new MenuItem(menu, SWT.CASCADE);
		remoteMenuItem.setText(Messages.HerokuContributionItem_RemoteSubmenuLabel);
		Menu remoteSubMenu = new Menu(menu);

		createDeploySubMenuItem(remoteSubMenu, "Console", BUNDLE_HEROKU); //$NON-NLS-1$
		createDeploySubMenuItem(remoteSubMenu, "Rake Command", BUNDLE_HEROKU); //$NON-NLS-1$

		remoteMenuItem.setMenu(remoteSubMenu);

		// config vars
		MenuItem configMenuItem = new MenuItem(menu, SWT.CASCADE);
		configMenuItem.setText(Messages.HerokuContributionItem_ConfigVarsSubmenuLabel);
		Menu configSubMenu = new Menu(menu);

		createDeploySubMenuItem(configSubMenu, "Add Config Var", BUNDLE_HEROKU); //$NON-NLS-1$
		createDeploySubMenuItem(configSubMenu, "Clear Config Vars", BUNDLE_HEROKU); //$NON-NLS-1$

		configMenuItem.setMenu(configSubMenu);

		// may want to add backup commands
		createDeploySubMenuItem(menu, "App Info", BUNDLE_HEROKU); //$NON-NLS-1$
		createDeploySubMenuItem(menu, "Rename App", BUNDLE_HEROKU); //$NON-NLS-1$
	}
}
