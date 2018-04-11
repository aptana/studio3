/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator.actions;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class FileSystemNewFromTemplateAction extends BaseSelectionListenerAction
{

	private static class MenuCreator implements IMenuCreator
	{

		private MenuManager dropDownMenuMgr;

		protected MenuCreator()
		{
		}

		public void dispose()
		{
			if (dropDownMenuMgr != null)
			{
				dropDownMenuMgr.dispose();
				dropDownMenuMgr = null;
			}
		}

		public Menu getMenu(Control parent)
		{
			createDropDownMenuMgr();
			return dropDownMenuMgr.createContextMenu(parent);
		}

		public Menu getMenu(Menu parent)
		{
			createDropDownMenuMgr();

			Menu menu = new Menu(parent);
			IContributionItem[] items = dropDownMenuMgr.getItems();
			for (IContributionItem item : items)
			{
				if (item instanceof ActionContributionItem)
				{
					item = new ActionContributionItem(((ActionContributionItem) item).getAction());
				}
				item.fill(menu, -1);
			}
			return menu;
		}

		private void createDropDownMenuMgr()
		{
			if (dropDownMenuMgr == null)
			{
				dropDownMenuMgr = new MenuManager();
				dropDownMenuMgr.add(new NewFileTemplateMenuContributor());
			}
		}
	};

	public FileSystemNewFromTemplateAction(IWorkbenchWindow window)
	{
		super(Messages.FileSystemNewFromTemplateAction_Text);
		setMenuCreator(new MenuCreator());
	}
}
