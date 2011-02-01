/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator;

import org.eclipse.core.resources.IWorkspaceRoot;

public class ProjectExplorerContentProvider extends FileTreeContentProvider
{

	@Override
	public Object[] getElements(Object inputElement)
	{
		if (inputElement instanceof IWorkspaceRoot)
		{
			return new Object[] { LocalFileSystems.getInstance() };
		}
		return super.getElements(inputElement);
	}
}
