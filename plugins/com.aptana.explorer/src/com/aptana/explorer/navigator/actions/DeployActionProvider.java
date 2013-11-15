/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.explorer.navigator.actions;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class DeployActionProvider extends ExplorerActionProvider
{

	private static final String DEPLOY_PLUGIN_ID = "com.aptana.deploy.ui"; //$NON-NLS-1$
	private static final String HOT_IMAGE_PATH = "/icons/full/elcl16/deploy_package_hot.png"; //$NON-NLS-1$
	private static final String IMAGE_PATH = "/icons/full/elcl16/deploy_package.png"; //$NON-NLS-1$
	private static final String MENU_ID = "com.aptana.explorer.deploy"; //$NON-NLS-1$
	public static final String ID = "com.aptana.explorer.navigator.actions.DeployActions"; //$NON-NLS-1$

	@Override
	public String getActionId()
	{
		return ID;
	}

	@Override
	protected Image getImage()
	{
		return AbstractUIPlugin.imageDescriptorFromPlugin(DEPLOY_PLUGIN_ID, IMAGE_PATH).createImage();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ui.actions.DefaultNavigatorActionProvider#getHotImage()
	 */
	@Override
	protected Image getHotImage()
	{
		return AbstractUIPlugin.imageDescriptorFromPlugin(DEPLOY_PLUGIN_ID, HOT_IMAGE_PATH).createImage();
	}

	@Override
	protected String getMenuId()
	{
		return MENU_ID;
	}

	@Override
	protected String getToolTip()
	{
		return Messages.DeployActionProvider_TTP_Deploy;
	}
}
