/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.aptana.ide.syncing.ui.SyncingUIPlugin;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer
{

	/**
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences()
	{
		IPreferenceStore store = SyncingUIPlugin.getDefault().getPreferenceStore();
		store.setDefault(IPreferenceConstants.VIEW_MODE, IPreferenceConstants.VIEW_FLAT);
		store.setDefault(IPreferenceConstants.DIRECTION_MODE, IPreferenceConstants.DIRECTION_BOTH);
		store.setDefault(IPreferenceConstants.SHOW_SYNC_EXPLORER_TABLE, true);
		store.setDefault(IPreferenceConstants.SHOW_DATE, true);
		store.setDefault(IPreferenceConstants.SHOW_SIZE, true);
		store.setDefault(IPreferenceConstants.FILE_PERMISSION, "-rw-rw-rw-"); //$NON-NLS-1$
		store.setDefault(IPreferenceConstants.DIRECTORY_PERMISSION, "drwxrwxrwx"); //$NON-NLS-1$
	}
}
