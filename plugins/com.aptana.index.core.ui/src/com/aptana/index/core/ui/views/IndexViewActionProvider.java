/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.index.core.ui.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.action.IAction;

/**
 * IndexViewerActionProvider
 */
public class IndexViewActionProvider extends AbstractProvider<IActionProvider> implements IActionProvider
{

	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.ui.views.IActionProvider#getActions(com.aptana.index.core.ui.views.IndexView,
	 * java.lang.Object)
	 */
	public IAction[] getActions(IndexView view, Object object)
	{
		List<IActionProvider> providers = getProcessors();
		List<IAction> result = new ArrayList<IAction>();

		for (IActionProvider provider : providers)
		{
			IAction[] actions = provider.getActions(view, object);

			if (actions != null && actions.length > 0)
			{
				result.addAll(Arrays.asList(actions));
			}
		}

		return (!result.isEmpty()) ? result.toArray(new IAction[result.size()]) : null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.ui.views.AbstractProvider#getAttributeName()
	 */
	@Override
	public String getAttributeName()
	{
		return "action-provider"; //$NON-NLS-1$
	}

}
