/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.aptana.ide.syncing.ui.actions.SynchronizeAction;

public class SynchronizeHandler extends BaseSyncHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		SynchronizeAction action = new SynchronizeAction();
		action.setActivePart(null, HandlerUtil.getActivePart(event));
		action.setSelection(new StructuredSelection(getSelectedResources()), isSelectionFromSource());
		action.run(null);

		return null;
	}
}
