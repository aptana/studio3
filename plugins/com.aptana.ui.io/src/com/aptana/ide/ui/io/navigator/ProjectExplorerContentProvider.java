/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IWorkspaceRoot;

import com.aptana.ide.core.io.CoreIOPlugin;

public class ProjectExplorerContentProvider extends FileTreeContentProvider
{

	private static final String LOCAL_SHORTCUTS_ID = "com.aptana.ide.core.io.localShortcuts"; //$NON-NLS-1$

	@Override
	public Object[] getElements(Object inputElement)
	{
		if (inputElement instanceof IWorkspaceRoot)
		{
			List<Object> children = new ArrayList<Object>();
			children.add(LocalFileSystems.getInstance());
			children.add(CoreIOPlugin.getConnectionPointManager().getConnectionPointCategory(LOCAL_SHORTCUTS_ID));
			return children.toArray(new Object[children.size()]);
		}
		return super.getElements(inputElement);
	}
}
