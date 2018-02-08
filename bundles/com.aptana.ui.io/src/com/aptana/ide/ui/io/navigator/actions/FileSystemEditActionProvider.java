/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator.actions;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;

public class FileSystemEditActionProvider extends CommonActionProvider
{

	private ActionGroup fEditActionGroup;

	public FileSystemEditActionProvider()
	{
	}

	@Override
	public void init(ICommonActionExtensionSite aSite)
	{
		super.init(aSite);

		fEditActionGroup = createEditActionGroup(aSite);
	}

	@Override
	public void dispose()
	{
		fEditActionGroup.dispose();
	}

	@Override
	public void fillActionBars(IActionBars actionBars)
	{
		fEditActionGroup.fillActionBars(actionBars);
	}

	@Override
	public void fillContextMenu(IMenuManager menu)
	{
		fEditActionGroup.fillContextMenu(menu);
	}

	@Override
	public void setContext(ActionContext context)
	{
		fEditActionGroup.setContext(context);
	}

	@Override
	public void updateActionBars()
	{
		fEditActionGroup.updateActionBars();
	}

	protected ActionGroup createEditActionGroup(ICommonActionExtensionSite aSite)
	{
		return new FileSystemEditActionGroup(aSite.getViewSite().getShell(), (Tree) aSite.getStructuredViewer()
				.getControl());
	}
}
