/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator.actions;

import org.eclipse.ui.actions.BaseSelectionListenerAction;

import com.aptana.ide.ui.io.actions.DeleteConnectionAction;

public class ConnectionDeleteAction extends BaseSelectionListenerAction
{

	public ConnectionDeleteAction()
	{
		super(Messages.FileSystemDeleteAction_Text);
	}

	public void run()
	{
		DeleteConnectionAction action = new DeleteConnectionAction();
		action.selectionChanged(this, getStructuredSelection());
		action.run(this);
	}
}
