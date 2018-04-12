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
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

import com.aptana.ide.syncing.core.SiteConnectionUtils;

public class NavigatorBaseSyncAction extends BaseSelectionListenerAction
{

	private IWorkbenchPart fActivePart;
	// a flag indicating if the selected elements belongs to the source or destination within a sync connection
	// by default, assume the selection is from source
	private boolean fSelectedFromSource = true;

	public NavigatorBaseSyncAction(String text, IWorkbenchPart activePart)
	{
		super(text);
		fActivePart = activePart;
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection)
	{
		// checks if any of the selection belongs to a sync connection and enables the action accordingly
		Object[] elements = ((IStructuredSelection) selection).toArray();
		for (Object element : elements)
		{
			if (element instanceof IAdaptable)
			{
				if (SiteConnectionUtils.findSitesForSource((IAdaptable) element).length > 0)
				{
					fSelectedFromSource = true;
					return true;
				}
				if (SiteConnectionUtils.findSitesWithDestination((IAdaptable) element).length > 0)
				{
					fSelectedFromSource = false;
					return true;
				}
			}
		}
		return false;
	}

	protected IWorkbenchPart getActivePart()
	{
		return fActivePart;
	}

	protected boolean isSelectionFromSource()
	{
		return fSelectedFromSource;
	}
}
