/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.preferences;

import java.text.MessageFormat;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.build.IBuildParticipant.BuildType;
import com.aptana.core.build.PreferenceUtil;
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.js.IJSConstants;
import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.validator.JSLintValidator;
import com.aptana.editor.js.validator.JSParserValidator;

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
		IEclipsePreferences prefs = (EclipseUtil.defaultScope()).getNode(JSPlugin.PLUGIN_ID);

		prefs.putBoolean(IPreferenceConstants.COMMENT_INDENT_USE_STAR, DEFAULT_COMMENT_INDENT_USE_STAR);
		// prefs.putBoolean(com.aptana.editor.common.preferences.IPreferenceConstants.LINK_OUTLINE_WITH_EDITOR, true);
		prefs.putDouble(IPreferenceConstants.JS_INDEX_VERSION, 0);
		prefs.put(
				com.aptana.editor.common.contentassist.IPreferenceConstants.COMPLETION_PROPOSAL_ACTIVATION_CHARACTERS,
				"."); //$NON-NLS-1$
		prefs.put(
				com.aptana.editor.common.contentassist.IPreferenceConstants.CONTEXT_INFORMATION_ACTIVATION_CHARACTERS,
				".("); //$NON-NLS-1$
		// https://jira.appcelerator.org/browse/APSTUD-4665
//		prefs.put(com.aptana.editor.common.contentassist.IPreferenceConstants.PROPOSAL_TRIGGER_CHARACTERS, "."); //$NON-NLS-1$
		prefs.put(com.aptana.editor.common.contentassist.IPreferenceConstants.PROPOSAL_TRIGGER_CHARACTERS, ""); //$NON-NLS-1$
		prefs.putBoolean(com.aptana.editor.common.preferences.IPreferenceConstants.EDITOR_AUTO_INDENT, true);
		prefs.putBoolean(com.aptana.editor.common.preferences.IPreferenceConstants.EDITOR_ENABLE_FOLDING, true);

		// mark occurrences
		// prefs.putBoolean(com.aptana.editor.common.preferences.IPreferenceConstants.EDITOR_MARK_OCCURRENCES, true);

		// Set up JS Parser validator to be on for build and reconcile
		prefs.putBoolean(PreferenceUtil.getEnablementPreferenceKey(JSParserValidator.ID, BuildType.BUILD), true);
		prefs.putBoolean(PreferenceUtil.getEnablementPreferenceKey(JSParserValidator.ID, BuildType.RECONCILE), true);

		// Set up JSLint prefs
		// Set default options
		// @formatter:off
		prefs.put(JSLintValidator.JS_LINT_OPTIONS, "{\n" + //$NON-NLS-1$
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
		IEclipsePreferences cepPrefs = EclipseUtil.instanceScope().getNode(CommonEditorPlugin.PLUGIN_ID);
		String oldKey = MessageFormat.format("{0}:{1}", IJSConstants.CONTENT_TYPE_JS, //$NON-NLS-1$
				com.aptana.editor.common.preferences.IPreferenceConstants.FILTER_EXPRESSIONS);
		String oldFilters = cepPrefs.get(oldKey, null);
		if (oldFilters != null)
		{
			try
			{
				String[] oldFilterArray = oldFilters.split(AbstractBuildParticipant.FILTER_DELIMITER);
				String[] combined = ArrayUtil.flatten(oldFilterArray, defaultJSLintFilters);

				IEclipsePreferences newPrefs = EclipseUtil.instanceScope().getNode(JSPlugin.PLUGIN_ID);
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
