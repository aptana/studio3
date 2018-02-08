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
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class FileSystemNewActionProvider extends CommonActionProvider
{

	private FileSystemNewAction fNewAction;
	private boolean fContribute;

	public FileSystemNewActionProvider()
	{
	}

	public void init(ICommonActionExtensionSite aSite)
	{
		super.init(aSite);

		if (aSite.getViewSite() instanceof ICommonViewerWorkbenchSite)
		{
			ICommonViewerWorkbenchSite viewSite = (ICommonViewerWorkbenchSite) aSite.getViewSite();
			fNewAction = new FileSystemNewAction(viewSite.getWorkbenchWindow());
			fContribute = true;
		}
	}

	public void fillContextMenu(IMenuManager menu)
	{
		if (fContribute)
		{
			fNewAction.selectionChanged(getSelection());
			menu.insertAfter(ICommonMenuConstants.GROUP_NEW, fNewAction);
		}
	}

	private IStructuredSelection getSelection()
	{
		return (IStructuredSelection) getContext().getSelection();
	}
}
