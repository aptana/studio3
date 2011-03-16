/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator.actions;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.navigator.ICommonMenuConstants;

public class ConnectionPointEditActionGroup extends ActionGroup
{

	private ConnectionDeleteAction fDeleteAction;

	public ConnectionPointEditActionGroup()
	{
		makeActions();
	}

	@Override
	public void fillContextMenu(IMenuManager menu)
	{
		menu.appendToGroup(ICommonMenuConstants.GROUP_EDIT, fDeleteAction);
	}

	@Override
	public void updateActionBars()
	{
		fDeleteAction.selectionChanged(getSelection());
	}

	protected void makeActions()
	{
		ISharedImages images = PlatformUI.getWorkbench().getSharedImages();

		fDeleteAction = new ConnectionDeleteAction();
		fDeleteAction.setDisabledImageDescriptor(images.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
		fDeleteAction.setImageDescriptor(images.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		fDeleteAction.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_DELETE);
		fDeleteAction.setAccelerator(SWT.DEL);
	}

	private IStructuredSelection getSelection()
	{
		return (IStructuredSelection) getContext().getSelection();
	}
}
