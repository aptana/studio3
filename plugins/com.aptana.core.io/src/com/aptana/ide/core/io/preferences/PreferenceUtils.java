/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.core.io.preferences;

import org.eclipse.core.runtime.Platform;

import com.aptana.ide.core.io.CoreIOPlugin;

public class PreferenceUtils {

    /**
     * @return the permissions for new files
     */
    public static long getFilePermissions() {
        return Platform.getPreferencesService().getLong(CoreIOPlugin.PLUGIN_ID,
                IPreferenceConstants.FILE_PERMISSION,
                PreferenceInitializer.DEFAULT_FILE_PERMISSIONS, null);
    }

    /**
     * @return the permissions for new directories
     */
    public static long getDirectoryPermissions() {
        return Platform.getPreferencesService().getLong(CoreIOPlugin.PLUGIN_ID,
                IPreferenceConstants.DIRECTORY_PERMISSION,
                PreferenceInitializer.DEFAULT_DIRECTORY_PERMISSIONS, null);
    }
}
