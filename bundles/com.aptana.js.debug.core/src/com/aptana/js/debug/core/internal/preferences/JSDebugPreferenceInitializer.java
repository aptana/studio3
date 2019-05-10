/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.internal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.js.debug.core.JSDebugPlugin;
import com.aptana.js.debug.core.preferences.IJSDebugPreferenceNames;

/**
 * @author Max Stepanov
 */
public class JSDebugPreferenceInitializer extends AbstractPreferenceInitializer
{

	/*
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences node = DefaultScope.INSTANCE.getNode(JSDebugPlugin.PLUGIN_ID);

		// default preferences
		node.putBoolean(IJSDebugPreferenceNames.SUSPEND_ON_ALL_EXCEPTIONS, false);
		node.putBoolean(IJSDebugPreferenceNames.SUSPEND_ON_UNCAUGHT_EXCEPTIONS, false);
	}
}
