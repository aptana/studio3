/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.decorators;

import org.eclipse.ui.IDecoratorManager;

import com.aptana.ide.syncing.ui.SyncingUIPlugin;

public class DecoratorUtils
{

	/**
	 * Refreshes the cloaking decorator.
	 */
	public static void updateCloakDecorator()
	{
		IDecoratorManager dm = SyncingUIPlugin.getDefault().getWorkbench().getDecoratorManager();
		dm.update("com.aptana.ide.syncing.ui.decorators.CloakedLabelDecorator"); //$NON-NLS-1$
	}
}
