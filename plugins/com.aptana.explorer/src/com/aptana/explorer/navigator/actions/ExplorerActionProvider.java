/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.explorer.navigator.actions;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.menus.IMenuService;
import org.eclipse.ui.menus.MenuUtil;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

public abstract class ExplorerActionProvider extends CommonActionProvider
{

	private IWorkbenchPartSite partSite;
	private boolean isToolbarFilled;

	@Override
	public void init(ICommonActionExtensionSite aSite)
	{
		super.init(aSite);

		partSite = ((ICommonViewerWorkbenchSite) aSite.getViewSite()).getSite();
	}

	@Override
	public void fillActionBars(IActionBars actionBars)
	{
		if (!isToolbarFilled)
		{
			fillToolBar(actionBars.getToolBarManager());
			actionBars.updateActionBars();
			isToolbarFilled = true;
		}
	}

	protected abstract Image getImage();

	protected abstract String getMenuID();

	protected IWorkbenchPartSite getPartSite()
	{
		return partSite;
	}

	protected String getToolTip()
	{
		return null;
	}

	/**
	 * Subclass could use the method to add additional menus programmatically. The default implementation does nothing.
	 * 
	 * @param menuManager
	 *            the menu manager
	 */
	protected void fillMenu(MenuManager menuManager)
	{
	}

	private void fillToolBar(IToolBarManager toolbarManager)
	{
		toolbarManager.add(new ContributionItem()
		{

			@Override
			public void fill(final ToolBar parent, int index)
			{
				ToolItem toolItem = new ToolItem(parent, SWT.DROP_DOWN);
				toolItem.setImage(getImage());
				toolItem.setToolTipText(getToolTip());

				toolItem.addSelectionListener(new SelectionAdapter()
				{

					@Override
					public void widgetSelected(SelectionEvent selectionEvent)
					{
						// makes clicking on the icon show the same menu as clicking on the drop-down arrow
						Point toolbarLocation = parent.getLocation();
						toolbarLocation = parent.getParent().toDisplay(toolbarLocation.x, toolbarLocation.y);
						Point toolbarSize = parent.getSize();
						MenuManager menuManager = new MenuManager(null, getMenuID());
						IMenuService menuService = (IMenuService) partSite.getService(IMenuService.class);
						menuService.populateContributionManager(menuManager, MenuUtil.menuUri(menuManager.getId()));
						fillMenu(menuManager);
						Menu menu = menuManager.createContextMenu(parent);
						menu.setLocation(toolbarLocation.x, toolbarLocation.y + toolbarSize.y + 2);
						menu.setVisible(true);
					}
				});
			}
		});
	}
}
