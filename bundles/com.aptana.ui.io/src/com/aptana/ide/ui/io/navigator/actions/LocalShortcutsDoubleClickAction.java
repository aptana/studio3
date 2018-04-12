/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator.actions;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

import com.aptana.ide.ui.io.dialogs.LocalConnectionPropertyDialog;

/**
 * @author Michael Xia (mxia@appcelerator.com)
 */
public class LocalShortcutsDoubleClickAction extends BaseDoubleClickAction
{

	private Shell fShell;

	public LocalShortcutsDoubleClickAction(Shell shell, TreeViewer treeViewer)
	{
		super(treeViewer);
		fShell = shell;
	}

	public void run()
	{
		if (selectionHasChildren())
		{
			super.run();
		}
		else
		{
			// no connection point has been defined; opens the new local shortcut dialog
			Dialog dialog = new LocalConnectionPropertyDialog(fShell);
			dialog.open();
		}
	}
}
