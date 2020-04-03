/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.core.build.IBuildParticipant.BuildType;
import com.aptana.core.build.IProblem;
import com.aptana.core.build.PreferenceUtil;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.preferences.IPreferenceConstants;
import com.aptana.js.internal.core.build.JSParserValidator;

public class PreferenceInitializer extends AbstractPreferenceInitializer
{
	public static final boolean DEFAULT_COMMENT_INDENT_USE_STAR = true;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences prefs = DefaultScope.INSTANCE.getNode(JSCorePlugin.PLUGIN_ID);

		prefs.putDouble(IPreferenceConstants.JS_INDEX_VERSION, 0);

		// Warn on missing semicolons
		prefs.put(IPreferenceConstants.PREF_MISSING_SEMICOLON_SEVERITY, IProblem.Severity.WARNING.id());

		// Set up JS Parser validator to be on for build and reconcile
		prefs.putBoolean(PreferenceUtil.getEnablementPreferenceKey(JSParserValidator.ID, BuildType.BUILD), true);
		prefs.putBoolean(PreferenceUtil.getEnablementPreferenceKey(JSParserValidator.ID, BuildType.RECONCILE), true);		
	}
}
