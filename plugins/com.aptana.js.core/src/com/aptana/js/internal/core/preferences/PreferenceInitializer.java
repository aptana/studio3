/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.preferences;

import java.text.MessageFormat;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.build.IBuildParticipant.BuildType;
import com.aptana.core.build.IProblem;
import com.aptana.core.build.PreferenceUtil;
import com.aptana.core.util.ArrayUtil;
import com.aptana.js.core.IJSConstants;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.preferences.IPreferenceConstants;
import com.aptana.js.internal.core.build.JSLintValidator;
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

		// Set up JSLint prefs
		// Set default options
		// @formatter:off
		prefs.put(IPreferenceConstants.JS_LINT_OPTIONS, "{\n" + //$NON-NLS-1$
				"  \"laxLineEnd\": true,\n" + //$NON-NLS-1$
				"  \"undef\": true,\n" + //$NON-NLS-1$
				"  \"browser\": true,\n" + //$NON-NLS-1$
				"  \"jscript\": true,\n" + //$NON-NLS-1$
				"  \"debug\": true,\n" + //$NON-NLS-1$
				"  \"maxerr\": 100000,\n" + //$NON-NLS-1$
				"  \"white\": true,\n" + //$NON-NLS-1$
				"  \"predef\": [\n" + //$NON-NLS-1$
				"    \"Ti\",\n" + //$NON-NLS-1$
				"    \"Titanium\",\n" + //$NON-NLS-1$
				"    \"alert\",\n" + //$NON-NLS-1$
				"    \"require\",\n" + //$NON-NLS-1$
				"    \"exports\",\n" + //$NON-NLS-1$
				"    \"native\",\n" + //$NON-NLS-1$
				"    \"implements\",\n" + //$NON-NLS-1$
				"  ]\n" + //$NON-NLS-1$
				"}"); //$NON-NLS-1$
		// @formatter:on

		// Set default JSLint filters
		String[] defaultJSLintFilters = new String[] {
				"Missing space between .+", "Unexpected '\\(space\\)'\\.", //$NON-NLS-1$ //$NON-NLS-2$
				"Expected '.+' at column \\d+, not column \\d+\\.", "Unexpected space between .+", "Expected exactly one space between .+" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		prefs.put(PreferenceUtil.getFiltersKey(JSLintValidator.ID),
				PreferenceUtil.serializeFilters(defaultJSLintFilters));

		// Migrate the old filter prefs to new validator
		IEclipsePreferences cepPrefs = InstanceScope.INSTANCE.getNode("com.aptana.editor.common"); //$NON-NLS-1$
		String oldKey = MessageFormat.format("{0}:{1}", IJSConstants.CONTENT_TYPE_JS, //$NON-NLS-1$
				"com.aptana.editor.common.filterExpressions"); //$NON-NLS-1$
		String oldFilters = cepPrefs.get(oldKey, null);
		if (oldFilters != null)
		{
			try
			{
				String[] oldFilterArray = oldFilters.split(AbstractBuildParticipant.FILTER_DELIMITER);
				String[] combined = ArrayUtil.flatten(oldFilterArray, defaultJSLintFilters);

				IEclipsePreferences newPrefs = InstanceScope.INSTANCE.getNode(JSCorePlugin.PLUGIN_ID);
				newPrefs.put(PreferenceUtil.getFiltersKey(JSLintValidator.ID),
						PreferenceUtil.serializeFilters(combined));
				newPrefs.flush();
				cepPrefs.remove(oldKey);
			}
			catch (BackingStoreException e)
			{
				// ignore
			}
		}
	}
}
