/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.navigator.actions;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.ui.dialogs.SiteConnectionsEditorDialog;
import com.aptana.ide.syncing.ui.editors.EditorUtils;
import com.aptana.ide.syncing.ui.navigator.ProjectSiteConnection;
import com.aptana.ide.ui.io.navigator.actions.BaseDoubleClickAction;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class DoubleClickAction extends BaseDoubleClickAction
{

	private Shell fShell;
	private TreeViewer fTreeViewer;

	public DoubleClickAction(Shell shell, TreeViewer treeViewer)
	{
		super(treeViewer);
		fShell = shell;
		fTreeViewer = treeViewer;
	}

	public void run()
	{
		IStructuredSelection selection = (IStructuredSelection) fTreeViewer.getSelection();
		Object element = selection.getFirstElement();
		if (element instanceof ISiteConnection)
		{
			// double-clicked on a site; opens it in the connection editor
			EditorUtils.openConnectionEditor((ISiteConnection) element);
		}
		else if (element instanceof ProjectSiteConnection)
		{
			// double-clicked on a site inside a project; both expands the node
			// and opens the connection editor
			super.run();
			EditorUtils.openConnectionEditor(((ProjectSiteConnection) element).getSiteConnection());
		}
		else
		{
			if (selectionHasChildren())
			{
				super.run();
			}
			else
			{
				// no connection point has been defined against the project;
				// opens the new site connection dialog
				IAdaptable source = null;
				if (element instanceof IAdaptable)
				{
					source = (IAdaptable) element;
				}
				openNewSiteConnectionDialog(source);
			}
		}
	}

	private void openNewSiteConnectionDialog(IAdaptable source)
	{
		SiteConnectionsEditorDialog dlg = new SiteConnectionsEditorDialog(fShell);
		dlg.setCreateNew(Messages.DoubleClickAction_NewConnection, source, null);
		dlg.open();
	}
}
