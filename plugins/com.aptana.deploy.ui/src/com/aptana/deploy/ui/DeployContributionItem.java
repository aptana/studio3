/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.ui;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.BundleEntry;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.CommandElement;

public class DeployContributionItem extends ContributionItem
{

	public DeployContributionItem()
	{
	}

	public DeployContributionItem(String id)
	{
		super(id);
	}

	@Override
	public boolean isDynamic()
	{
		return true;
	}

	protected static void createDeploySubMenuItem(Menu menu, String cmd, String bundle)
	{
		final CommandElement command;
		command = getBundleCommand(bundle, cmd);

		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText(cmd);
		item.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				command.execute();
			}
		});
	}

	protected static CommandElement getBundleCommand(String bundleName, String commandName)
	{
		BundleEntry entry = BundleManager.getInstance().getBundleEntry(bundleName);
		if (entry == null)
		{
			return null;
		}
		CommandElement command;
		for (BundleElement bundle : entry.getContributingBundles())
		{
			command = bundle.getCommandByName(commandName);
			if (command != null)
			{
				return command;
			}
		}
		return null;
	}
}
