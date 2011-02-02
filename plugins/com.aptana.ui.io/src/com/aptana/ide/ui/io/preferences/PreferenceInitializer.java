/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.aptana.ide.core.io.preferences.IPreferenceConstants;
import com.aptana.ide.ui.io.IOUIPlugin;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = IOUIPlugin.getDefault().getPreferenceStore();
        store.setDefault(IPreferenceConstants.GLOBAL_CLOAKING_EXTENSIONS,
                com.aptana.ide.core.io.preferences.PreferenceInitializer.DEFAULT_CLOAK_EXPRESSIONS);
    }
}
