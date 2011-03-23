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
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.CorePlugin;
import com.aptana.core.ICorePreferenceConstants;
import com.aptana.ui.Messages;
import com.aptana.ui.UIPlugin;

public class AptanaPreferencePage extends GenericRootPreferencePage
{

	protected static String PAGE_ID = "com.aptana.ui.AptanaPreferencePage"; //$NON-NLS-1$
	private Button debugButton;

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
		Font font = appearanceComposite.getFont();
		Group group = new Group(appearanceComposite, SWT.NONE);
		group.setFont(font);
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

		debugButton = new Button(comp, SWT.CHECK);
		debugButton.setText(Messages.AptanaPreferencePage_EnableDebugModeLabel);
		debugButton.setSelection(isInDebugMode());

		return comp;
	}

	private boolean isInDebugMode()
	{
		// Don't use EclipseUtil.isInDebugMode, because that also checks for osgi.debug system property or -debug flag
		return Platform.getPreferencesService().getBoolean(CorePlugin.PLUGIN_ID,
				ICorePreferenceConstants.PREF_DEBUG_MODE, false, null);
	}

	@Override
	public boolean performOk()
	{
		try
		{
			IEclipsePreferences prefs = new InstanceScope().getNode(CorePlugin.PLUGIN_ID);
			prefs.putBoolean(ICorePreferenceConstants.PREF_DEBUG_MODE, debugButton.getSelection());
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			UIPlugin.log(e);
			return false;
		}

		return super.performOk();
	}

	@Override
	protected void performDefaults()
	{
		try
		{
			IEclipsePreferences prefs = new InstanceScope().getNode(CorePlugin.PLUGIN_ID);
			prefs.remove(ICorePreferenceConstants.PREF_DEBUG_MODE);
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			UIPlugin.log(e);
		}
		debugButton.setSelection(isInDebugMode());

		super.performDefaults();
	}
}
