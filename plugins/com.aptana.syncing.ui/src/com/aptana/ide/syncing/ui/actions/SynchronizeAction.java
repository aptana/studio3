/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;

import com.aptana.ide.syncing.core.ISiteConnection;

public class SynchronizeAction extends BaseSyncAction
{

	public void run(IAction action)
	{
		openConnectionEditor();
	}

	protected void performAction(IAdaptable[] files, ISiteConnection site) throws CoreException
	{
	}
}
