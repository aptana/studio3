/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IWorkspaceRoot;

import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.IConnectionPointCategory;

public class RemoteTreeContentProvider extends FileTreeContentProvider
{

	@Override
	public Object[] getElements(Object inputElement)
	{
		if (inputElement instanceof IWorkspaceRoot)
		{
			// the top level would have all the connection points from remote categories
			List<IConnectionPoint> connections = new ArrayList<IConnectionPoint>();
			IConnectionPointCategory[] categories = CoreIOPlugin.getConnectionPointManager()
					.getConnectionPointCategories();
			for (IConnectionPointCategory category : categories)
			{
				if (category.isRemote())
				{
					connections.addAll(Arrays.asList(category.getConnectionPoints()));
				}
			}
			return connections.toArray(new IConnectionPoint[connections.size()]);
		}
		return super.getElements(inputElement);
	}
}
