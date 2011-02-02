/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator.actions;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;

public class ConnectionPointEditActionProvider extends CommonActionProvider
{

	private ConnectionPointEditActionGroup fEditActionGroup;

	public ConnectionPointEditActionProvider()
	{
	}

	public void init(ICommonActionExtensionSite aSite)
	{
		super.init(aSite);

		fEditActionGroup = new ConnectionPointEditActionGroup();
	}

	public void dispose()
	{
		fEditActionGroup.dispose();
	}

	public void fillActionBars(IActionBars actionBars)
	{
		fEditActionGroup.fillActionBars(actionBars);
	}

	public void fillContextMenu(IMenuManager menu)
	{
		fEditActionGroup.fillContextMenu(menu);
	}

	public void setContext(ActionContext context)
	{
		fEditActionGroup.setContext(context);
	}

	public void updateActionBars()
	{
		fEditActionGroup.updateActionBars();
	}
}
