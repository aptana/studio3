/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.core.build.IBuildParticipant.BuildType;
import com.aptana.core.build.IProblem;
import com.aptana.core.build.PreferenceUtil;
import com.aptana.editor.html.HTMLPlugin;
import com.aptana.editor.html.core.preferences.IPreferenceConstants;
import com.aptana.editor.html.validator.HTMLParserValidator;
import com.aptana.editor.html.validator.HTMLTidyValidator;

public class PreferenceInitializer extends AbstractPreferenceInitializer
{

	public static final String DEFAULT_TAG_ATTRIBUTES_TO_SHOW = "id class src href"; //$NON-NLS-1$

	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences prefs = DefaultScope.INSTANCE.getNode(HTMLPlugin.PLUGIN_ID);

		// prefs.putBoolean(com.aptana.editor.common.preferences.IPreferenceConstants.LINK_OUTLINE_WITH_EDITOR, true);
		prefs.putDouble(IPreferenceConstants.HTML_INDEX_VERSION, 0);
		prefs.put(
				com.aptana.editor.common.contentassist.IPreferenceConstants.COMPLETION_PROPOSAL_ACTIVATION_CHARACTERS,
				"</>=&'\" "); //$NON-NLS-1$
		prefs.putBoolean(IPreferenceConstants.HTML_AUTO_CLOSE_TAG_PAIRS, true);
		prefs.putBoolean(IPreferenceConstants.HTML_REMOTE_HREF_PROPOSALS,
				IPreferenceConstants.DEFAULT_REMOTE_HREF_PROPOSALS_VALUE);
		prefs.putBoolean(com.aptana.editor.common.preferences.IPreferenceConstants.EDITOR_AUTO_INDENT, true);
		prefs.putBoolean(com.aptana.editor.common.preferences.IPreferenceConstants.EDITOR_ENABLE_FOLDING, true);
		prefs.put(IPreferenceConstants.HTML_OUTLINE_TAG_ATTRIBUTES_TO_SHOW, DEFAULT_TAG_ATTRIBUTES_TO_SHOW);

		// mark occurrences
		// prefs.putBoolean(com.aptana.editor.common.preferences.IPreferenceConstants.EDITOR_MARK_OCCURRENCES, true);

		// Set Tidy validator to be on by default for reconcile
		prefs.putBoolean(PreferenceUtil.getEnablementPreferenceKey(HTMLTidyValidator.ID, BuildType.BUILD), false);
		prefs.putBoolean(PreferenceUtil.getEnablementPreferenceKey(HTMLTidyValidator.ID, BuildType.RECONCILE), true);
		prefs.putInt(HTMLTidyValidator.ProblemType.IdNameAttributeMismatch.getPrefKey(),
				IProblem.Severity.ERROR.intValue());

		// Set Parser Errors to be on by default for both
		prefs.putBoolean(PreferenceUtil.getEnablementPreferenceKey(HTMLParserValidator.ID, BuildType.BUILD), true);
		prefs.putBoolean(PreferenceUtil.getEnablementPreferenceKey(HTMLParserValidator.ID, BuildType.RECONCILE), true);
	}
}
