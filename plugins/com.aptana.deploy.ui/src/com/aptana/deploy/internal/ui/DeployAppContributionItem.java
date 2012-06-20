/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.internal.ui;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.menus.IWorkbenchContribution;
import org.eclipse.ui.services.IServiceLocator;

import com.aptana.core.logging.IdeLog;
import com.aptana.deploy.IDeployProvider;
import com.aptana.deploy.ui.DeployUIPlugin;
import com.aptana.deploy.util.DeployProviderUtil;
import com.aptana.ui.util.UIUtils;

/**
 * The class adds the "Deploy App" menu item. The item needs to be dynamic as the actual text could be contributed by
 * the individual deploy provider.
 * 
 * @author Michael Xia (mxia@appcelerator.com)
 */
public class DeployAppContributionItem extends ContributionItem implements IWorkbenchContribution
{

	private static final String DEPLOY_COMMAND_ID = "com.aptana.deploy.commands.deployApp"; //$NON-NLS-1$

	private IServiceLocator serviceLocator;

	public DeployAppContributionItem()
	{
	}

	public DeployAppContributionItem(String id)
	{
		super(id);
	}

	@Override
	public void fill(Menu menu, int index)
	{
		MenuItem deployMenuItem = new MenuItem(menu, SWT.PUSH);

		String menuName = null;
		IContainer selectedContainer = getSelectedContainer();
		if (selectedContainer != null)
		{
			IDeployProvider provider = DeployProviderUtil.getDeployProvider(selectedContainer);
			if (provider != null)
			{
				menuName = provider.getDeployMenuName();
			}
		}
		if (menuName == null)
		{
			// falls back to the name defined for the deploy command
			ICommandService commandService = (ICommandService) serviceLocator.getService(ICommandService.class);
			Command command = commandService.getCommand(DEPLOY_COMMAND_ID);
			try
			{
				menuName = command.getName();
			}
			catch (NotDefinedException e)
			{
				// should not happen, but log it just in case
				IdeLog.logError(DeployUIPlugin.getDefault(), "The name for the deploy command is not defined."); //$NON-NLS-1$
			}
		}
		// the default-default for the menu name is "Deploy App"
		deployMenuItem.setText((menuName == null) ? Messages.DeployAppContributionItem_Text : menuName);

		deployMenuItem.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				IHandlerService handlerService = (IHandlerService) serviceLocator.getService(IHandlerService.class);
				try
				{
					handlerService.executeCommand(DEPLOY_COMMAND_ID, null);
				}
				catch (Exception e1)
				{
					IdeLog.logError(DeployUIPlugin.getDefault(),
							"Failed to execute the command to deploy the application."); //$NON-NLS-1$
				}
			}
		});
	}

	public void initialize(IServiceLocator serviceLocator)
	{
		this.serviceLocator = serviceLocator;
	}

	private static IContainer getSelectedContainer()
	{
		IResource selectedResource = UIUtils.getSelectedResource();
		if (selectedResource instanceof IContainer)
		{
			return (IContainer) selectedResource;
		}
		if (selectedResource != null)
		{
			return selectedResource.getParent();
		}
		return null;
	}
}
