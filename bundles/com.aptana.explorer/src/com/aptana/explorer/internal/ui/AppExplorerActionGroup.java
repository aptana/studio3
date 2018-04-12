/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.explorer.internal.ui;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.internal.navigator.CommonNavigatorActionGroup;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.LinkHelperService;

@SuppressWarnings("restriction")
public class AppExplorerActionGroup extends CommonNavigatorActionGroup
{

	public AppExplorerActionGroup(CommonNavigator aNavigator, CommonViewer aViewer, LinkHelperService linkHelperService)
	{
		super(aNavigator, aViewer, linkHelperService);
	}

	@Override
	protected void fillToolBar(IToolBarManager toolBar)
	{
		super.fillToolBar(toolBar);
		// removes Link with Editor from the toolbar
		IContributionItem[] items = toolBar.getItems();
		for (IContributionItem item : items)
		{
			if (item instanceof ActionContributionItem)
			{
				if (IWorkbenchCommandConstants.NAVIGATE_TOGGLE_LINK_WITH_EDITOR.equals(((ActionContributionItem) item)
						.getAction().getActionDefinitionId()))
				{
					toolBar.remove(item);
					break;
				}
			}
		}
	}
}
