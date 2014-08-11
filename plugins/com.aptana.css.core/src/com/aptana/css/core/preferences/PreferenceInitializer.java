/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.build.IBuildParticipant.BuildType;
import com.aptana.core.build.PreferenceUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.css.core.CSSCorePlugin;
import com.aptana.css.core.internal.build.CSSParserValidator;
import com.aptana.css.core.internal.build.CSSValidator;

public class PreferenceInitializer extends AbstractPreferenceInitializer
{

	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences prefs = DefaultScope.INSTANCE.getNode(CSSCorePlugin.PLUGIN_ID);

		prefs.putDouble(IPreferenceConstants.CSS_INDEX_VERSION, 0);

		// Set CSS Stylesheet validator to be on by default for reconcile, off for build, use default set of filters.
		prefs.putBoolean(PreferenceUtil.getEnablementPreferenceKey(CSSValidator.ID, BuildType.BUILD), false);
		prefs.putBoolean(PreferenceUtil.getEnablementPreferenceKey(CSSValidator.ID, BuildType.RECONCILE), true);
		prefs.put(PreferenceUtil.getFiltersKey(CSSValidator.ID),
				StringUtil.join(AbstractBuildParticipant.FILTER_DELIMITER, CSSValidator.DEFAULT_FILTERS));

		// Set up CSS Parser validator to be on for build and reconcile
		prefs.putBoolean(PreferenceUtil.getEnablementPreferenceKey(CSSParserValidator.ID, BuildType.BUILD), true);
		prefs.putBoolean(PreferenceUtil.getEnablementPreferenceKey(CSSParserValidator.ID, BuildType.BUILD), true);
	}
}
