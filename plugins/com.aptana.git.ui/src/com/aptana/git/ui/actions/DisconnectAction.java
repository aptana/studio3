/*******************************************************************************
 * Copyright (C) 2007, Robin Rosenberg <robin.rosenberg@dewire.com>
 * Copyright (C) 2007, Shawn O. Pearce <spearce@spearce.org>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.aptana.git.ui.actions;

import java.util.List;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.jface.action.IAction;

import com.aptana.git.ui.internal.actions.DisconnectProviderOperation;

/**
 * Action to disassociate a project from its Git repository.
 * 
 * @see DisconnectProviderOperation
 */
public class DisconnectAction extends AbstractOperationAction
{
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected IWorkspaceRunnable createOperation(final IAction act, final List sel)
	{
		return sel.isEmpty() ? null : new DisconnectProviderOperation(sel);
	}

	protected void postOperation()
	{
		// do nothing
	}

	public void run()
	{
		run(null);
	}
}
