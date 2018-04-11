/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator.actions;

import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.eclipse.ui.actions.DeleteResourceAction;

public class ResourceEditActionGroup extends FileSystemEditActionGroup
{

	public ResourceEditActionGroup(Shell shell, Tree tree)
	{
		super(shell, tree);
	}

	@Override
	protected BaseSelectionListenerAction createDeleteAction(final Shell shell)
	{
		return new DeleteResourceAction(new IShellProvider()
		{

			public Shell getShell()
			{
				return shell;
			}
		});
	}
}
