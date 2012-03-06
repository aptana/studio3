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

import com.aptana.core.build.IBuildParticipant.BuildType;
import com.aptana.core.util.EclipseUtil;
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

		// Set validator to be on by default for reconcile
		CSSValidator validator = new CSSValidator()
		{
			@Override
			public String getId()
			{
				return ID;
			}

			@Override
			protected String getPreferenceNode()
			{
				return CSSPlugin.PLUGIN_ID;
			}
		};
		prefs.putBoolean(validator.getEnablementPreferenceKey(BuildType.BUILD), false);
		prefs.putBoolean(validator.getEnablementPreferenceKey(BuildType.RECONCILE), true);
		validator.setFilters(EclipseUtil.defaultScope(), CSSValidator.DEFAULT_FILTERS);

		CSSParserValidator parseValidator = new CSSParserValidator()
		{
			@Override
			public String getId()
			{
				return ID;
			}

			@Override
			protected String getPreferenceNode()
			{
				return CSSPlugin.PLUGIN_ID;
			}
		};
		prefs.putBoolean(parseValidator.getEnablementPreferenceKey(BuildType.BUILD), true);
		prefs.putBoolean(parseValidator.getEnablementPreferenceKey(BuildType.RECONCILE), true);

		// Migrate the old filter prefs to new
		IEclipsePreferences cepPrefs = EclipseUtil.instanceScope().getNode(CommonEditorPlugin.PLUGIN_ID);
		String oldKey = MessageFormat.format("{0}:{1}", ICSSConstants.CONTENT_TYPE_CSS,
				com.aptana.editor.common.preferences.IPreferenceConstants.FILTER_EXPRESSIONS);
		String oldFilters = cepPrefs.get(oldKey, null);
		if (oldFilters != null)
		{
			String[] oldFilterArray = oldFilters.split(CSSValidator.FILTER_DELIMITER);
			validator.setFilters(EclipseUtil.instanceScope(), oldFilterArray);
			cepPrefs.remove(oldKey);
		}
	}
}
