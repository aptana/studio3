/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.preferences;

import java.text.MessageFormat;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.build.PreferenceUtil;
import com.aptana.core.build.IBuildParticipant.BuildType;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.css.CSSPlugin;
import com.aptana.editor.css.ICSSConstants;
import com.aptana.editor.css.validator.CSSParserValidator;
import com.aptana.editor.css.validator.CSSValidator;

public class PreferenceInitializer extends AbstractPreferenceInitializer
{

	@SuppressWarnings("nls")
	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences prefs = EclipseUtil.defaultScope().getNode(CSSPlugin.PLUGIN_ID);

		// prefs.putBoolean(com.aptana.editor.common.preferences.IPreferenceConstants.LINK_OUTLINE_WITH_EDITOR, true);
		prefs.putDouble(IPreferenceConstants.CSS_INDEX_VERSION, 0);
		prefs.putBoolean(com.aptana.editor.common.preferences.IPreferenceConstants.EDITOR_AUTO_INDENT, true);
		prefs.putBoolean(com.aptana.editor.common.preferences.IPreferenceConstants.EDITOR_ENABLE_FOLDING, true);
		prefs.put(
				com.aptana.editor.common.contentassist.IPreferenceConstants.COMPLETION_PROPOSAL_ACTIVATION_CHARACTERS,
				".#:"); //$NON-NLS-1$
		prefs.put(
				com.aptana.editor.common.contentassist.IPreferenceConstants.CONTEXT_INFORMATION_ACTIVATION_CHARACTERS,
				"(,"); //$NON-NLS-1$
		prefs.put(com.aptana.editor.common.contentassist.IPreferenceConstants.PROPOSAL_TRIGGER_CHARACTERS, ""); //$NON-NLS-1$

		// mark occurrences
		// prefs.putBoolean(com.aptana.editor.common.preferences.IPreferenceConstants.EDITOR_MARK_OCCURRENCES, true);

		// Set CSS Stylesheet validator to be on by default for reconcile, off for build, use default set of filters.
		prefs.putBoolean(PreferenceUtil.getEnablementPreferenceKey(CSSValidator.ID, BuildType.BUILD),
				false);
		prefs.putBoolean(
				PreferenceUtil.getEnablementPreferenceKey(CSSValidator.ID, BuildType.RECONCILE), true);
		prefs.put(PreferenceUtil.getFiltersKey(CSSValidator.ID),
				StringUtil.join(AbstractBuildParticipant.FILTER_DELIMITER, CSSValidator.DEFAULT_FILTERS));

		// Migrate the old filter prefs to new
		IEclipsePreferences cepPrefs = EclipseUtil.instanceScope().getNode(CommonEditorPlugin.PLUGIN_ID);
		String oldKey = MessageFormat.format("{0}:{1}", ICSSConstants.CONTENT_TYPE_CSS,
				com.aptana.editor.common.preferences.IPreferenceConstants.FILTER_EXPRESSIONS);
		String oldFilters = cepPrefs.get(oldKey, null);
		if (oldFilters != null)
		{
			try
			{
				IEclipsePreferences newPrefs = EclipseUtil.instanceScope().getNode(CSSPlugin.PLUGIN_ID);
				newPrefs.put(PreferenceUtil.getFiltersKey(CSSValidator.ID), oldFilters);
				newPrefs.flush();
				cepPrefs.remove(oldKey);
			}
			catch (BackingStoreException e)
			{
				// ignore
			}
		}

		// Set up CSS Parser validator to be on for build and reconcile
		prefs.putBoolean(
				PreferenceUtil.getEnablementPreferenceKey(CSSParserValidator.ID, BuildType.BUILD), true);
		prefs.putBoolean(
				PreferenceUtil.getEnablementPreferenceKey(CSSParserValidator.ID, BuildType.BUILD), true);

	}
}
