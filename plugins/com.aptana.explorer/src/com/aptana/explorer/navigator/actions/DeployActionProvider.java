/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.explorer.navigator.actions;

import org.eclipse.swt.graphics.Image;

import com.aptana.explorer.ExplorerPlugin;
import com.aptana.explorer.IExplorerUIConstants;
import com.aptana.ui.actions.DefaultNavigatorActionProvider;

public class DeployActionProvider extends DefaultNavigatorActionProvider
{

	@Override
	protected Image getImage()
	{
		return ExplorerPlugin.getImage(IExplorerUIConstants.DEPLOY_MENU_ICON);
	}

	@Override
	protected String getMenuId()
	{
		return IExplorerUIConstants.DEPLOY_MENU_ID;
	}

	@Override
	protected String getToolTip()
	{
		return Messages.DeployActionProvider_TTP_Deploy;
	}
}
