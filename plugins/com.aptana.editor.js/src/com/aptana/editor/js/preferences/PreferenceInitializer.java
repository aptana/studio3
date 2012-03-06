/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.preferences;

import java.text.MessageFormat;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.build.IBuildParticipant.BuildType;
import com.aptana.core.util.EclipseUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.js.IJSConstants;
import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.validator.JSLintValidator;
import com.aptana.editor.js.validator.JSParserValidator;
import com.aptana.editor.js.validator.MozillaJsValidator;

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
		prefs.put(com.aptana.editor.common.contentassist.IPreferenceConstants.PROPOSAL_TRIGGER_CHARACTERS, "."); //$NON-NLS-1$
		prefs.putBoolean(com.aptana.editor.common.preferences.IPreferenceConstants.EDITOR_AUTO_INDENT, true);
		prefs.putBoolean(com.aptana.editor.common.preferences.IPreferenceConstants.EDITOR_ENABLE_FOLDING, true);

		// mark occurrences
		// prefs.putBoolean(com.aptana.editor.common.preferences.IPreferenceConstants.EDITOR_MARK_OCCURRENCES, true);

		// Set Mozilla validator to be on by default for reconcile (JSLint is off by default)
		MozillaJsValidator mozValidator = new MozillaJsValidator()
		{
			@Override
			public String getId()
			{
				return ID;
			}

			@Override
			protected String getPreferenceNode()
			{
				return JSPlugin.PLUGIN_ID;
			}
		};
		prefs.putBoolean(mozValidator.getEnablementPreferenceKey(BuildType.BUILD), false);
		prefs.putBoolean(mozValidator.getEnablementPreferenceKey(BuildType.RECONCILE), true);

		JSParserValidator parseValidator = new JSParserValidator()
		{
			@Override
			public String getId()
			{
				return ID;
			}

			@Override
			protected String getPreferenceNode()
			{
				return JSPlugin.PLUGIN_ID;
			}
		};
		prefs.putBoolean(parseValidator.getEnablementPreferenceKey(BuildType.BUILD), true);
		prefs.putBoolean(parseValidator.getEnablementPreferenceKey(BuildType.RECONCILE), true);

		// Migrate the old filter prefs to new validators
		IEclipsePreferences cepPrefs = EclipseUtil.instanceScope().getNode(CommonEditorPlugin.PLUGIN_ID);
		String oldKey = MessageFormat.format("{0}:{1}", IJSConstants.CONTENT_TYPE_JS, //$NON-NLS-1$
				com.aptana.editor.common.preferences.IPreferenceConstants.FILTER_EXPRESSIONS);
		String oldFilters = cepPrefs.get(oldKey, null);
		if (oldFilters != null)
		{
			JSLintValidator jsLintValidator = new JSLintValidator()
			{
				@Override
				public String getId()
				{
					return ID;
				}

				@Override
				protected String getPreferenceNode()
				{
					return JSPlugin.PLUGIN_ID;
				}
			};

			String[] oldFilterArray = oldFilters.split(AbstractBuildParticipant.FILTER_DELIMITER);
			mozValidator.setFilters(EclipseUtil.instanceScope(), oldFilterArray);
			jsLintValidator.setFilters(EclipseUtil.instanceScope(), oldFilterArray);
			cepPrefs.remove(oldKey);
		}
	}
}
