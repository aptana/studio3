/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.preferences;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.CorePlugin;
import com.aptana.core.ICorePreferenceConstants;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.ui.UIPlugin;

public class AptanaPreferencePage extends GenericRootPreferencePage
{

	protected static String PAGE_ID = "com.aptana.ui.AptanaPreferencePage"; //$NON-NLS-1$

	private Button migrateButton;
	private Button autoRefreshButton;

	@Override
	protected String getPageId()
	{
		return PAGE_ID;
	}

	/**
	 * Creates a field editor group for use in grouping items on a page
	 * 
	 * @param appearanceComposite
	 * @param string
	 * @return Composite
	 */
	public static Composite createGroup(Composite appearanceComposite, String string)
	{
		Group group = new Group(appearanceComposite, SWT.NONE);
		group.setFont(appearanceComposite.getFont());
		group.setText(string);

		group.setLayout(GridLayoutFactory.fillDefaults().margins(5, 5).numColumns(2).create());
		group.setLayoutData(GridDataFactory.fillDefaults().span(2, 0).grab(true, false).create());

		Composite c = new Composite(group, SWT.NONE);
		c.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());

		return c;
	}

	@Override
	protected Control createContents(Composite parent)
	{
		Composite comp = (Composite) super.createContents(parent);

		migrateButton = new Button(comp, SWT.CHECK);
		migrateButton.setText(Messages.AptanaPreferencePage_Auto_Migrate_Projects);
		migrateButton.setSelection(autoMigration());

		autoRefreshButton = new Button(comp, SWT.CHECK);
		autoRefreshButton.setText(Messages.AptanaPreferencePage_Auto_Refresh_Projects);
		autoRefreshButton.setSelection(autoRefresh());

		return comp;
	}

	private static boolean autoMigration()
	{
		return Platform.getPreferencesService().getBoolean(CorePlugin.PLUGIN_ID,
				ICorePreferenceConstants.PREF_AUTO_MIGRATE_OLD_PROJECTS,
				ICorePreferenceConstants.DEFAULT_AUTO_MIGRATE_OLD_PROJECTS, null);
	}

	private static boolean autoRefresh()
	{
		return Platform.getPreferencesService().getBoolean(CorePlugin.PLUGIN_ID,
				ICorePreferenceConstants.PREF_AUTO_REFRESH_PROJECTS,
				ICorePreferenceConstants.DEFAULT_AUTO_REFRESH_PROJECTS, null);
	}

	@Override
	public boolean performOk()
	{
		IEclipsePreferences prefs = EclipseUtil.instanceScope().getNode(CorePlugin.PLUGIN_ID);
		prefs.putBoolean(ICorePreferenceConstants.PREF_AUTO_MIGRATE_OLD_PROJECTS, migrateButton.getSelection());
		prefs.putBoolean(ICorePreferenceConstants.PREF_AUTO_REFRESH_PROJECTS, autoRefreshButton.getSelection());
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(UIPlugin.getDefault(), e);
		}

		return super.performOk();
	}

	@Override
	protected void performDefaults()
	{
		migrateButton.setSelection(Platform.getPreferencesService().getBoolean(CorePlugin.PLUGIN_ID,
				ICorePreferenceConstants.PREF_AUTO_MIGRATE_OLD_PROJECTS,
				ICorePreferenceConstants.DEFAULT_AUTO_MIGRATE_OLD_PROJECTS, null));
		autoRefreshButton.setSelection(Platform.getPreferencesService().getBoolean(CorePlugin.PLUGIN_ID,
				ICorePreferenceConstants.PREF_AUTO_REFRESH_PROJECTS,
				ICorePreferenceConstants.DEFAULT_AUTO_REFRESH_PROJECTS, null));

		super.performDefaults();
	}
}
