/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.menus.IWorkbenchContribution;
import org.eclipse.ui.services.IServiceLocator;

import com.aptana.theme.IThemeManager;
import com.aptana.theme.ThemePlugin;

public class SwitchThemesPulldownContributionItem extends ContributionItem implements IWorkbenchContribution
{

	public SwitchThemesPulldownContributionItem()
	{
	}

	public SwitchThemesPulldownContributionItem(String id)
	{
		super(id);
	}

	@Override
	public void fill(Menu menu, int index)
	{
		IThemeManager manager = ThemePlugin.getDefault().getThemeManager();
		List<String> themeNames = new ArrayList<String>(manager.getThemeNames());
		// sort ignoring case
		Collections.sort(themeNames, new Comparator<String>()
		{
			public int compare(String o1, String o2)
			{
				return o1.compareToIgnoreCase(o2);
			}
		});
		for (String name : themeNames)
		{
			IContributionItem item = new SwitchThemeContributionItem(manager, name);
			item.fill(menu, menu.getItemCount());
		}
	}

	public void initialize(IServiceLocator serviceLocator)
	{
		// ignore
	}

	private static void switchTheme(IThemeManager manager, String themeName)
	{
		manager.setCurrentTheme(manager.getTheme(themeName));
	}

	/**
	 * For a given theme, a menu entry to switch to it.
	 * 
	 * @author cwilliams
	 */
	private class SwitchThemeContributionItem extends ContributionItem
	{
		private IThemeManager manager;
		private String branchName;

		SwitchThemeContributionItem(IThemeManager repo, String themeName)
		{
			this.manager = repo;
			this.branchName = themeName;
		}

		@Override
		public void fill(Menu menu, int index)
		{
			MenuItem menuItem = new MenuItem(menu, SWT.PUSH, index);
			menuItem.setText(branchName);
			menuItem.setEnabled(!branchName.equals(manager.getCurrentTheme().getName()));
			menuItem.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					// what to do when menu is subsequently selected.
					switchTheme(manager, branchName);
				}
			});
		}
	}
}