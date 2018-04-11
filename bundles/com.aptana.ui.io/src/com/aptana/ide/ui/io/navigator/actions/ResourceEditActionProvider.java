/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator.actions;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;

public class ResourceEditActionProvider extends FileSystemEditActionProvider
{

	public ResourceEditActionProvider()
	{
	}

	@Override
	protected ActionGroup createEditActionGroup(ICommonActionExtensionSite aSite)
	{
		return new ResourceEditActionGroup(aSite.getViewSite().getShell(), (Tree) aSite.getStructuredViewer()
				.getControl());
	}
}
