/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.css.CSSPlugin;
import com.aptana.editor.css.ICSSConstants;

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

		prefs = EclipseUtil.defaultScope().getNode(CommonEditorPlugin.PLUGIN_ID);

		String[] filtered = new String[] { ".*Unknown pseudo-element.*", "Property\\s*[-_].*doesn't exist.*",
				".*-moz-.*", ".*-o-*", ".*opacity.*", ".*overflow-.*", ".*accelerator.*", ".*background-position-.*",
				".*filter.*", ".*ime-mode.*", ".*layout-.*", ".*line-break.*", ".*page.*", ".*ruby-.*",
				".*scrollbar-.*", ".*text-align-.*", ".*text-justify.*", ".*text-overflow.*", ".*text-shadow.*",
				".*text-underline-position.*", ".*word-spacing.*", ".*word-wrap.*", ".*writing-mode.*", ".*zoom.*",
				".*Parse Error.*", ".*-webkit-.*", ".*rgba.*is not a .* value.*",
				".*Too many values or values are not recognized.*" };

		prefs.put(ICSSConstants.CONTENT_TYPE_CSS + ":"
				+ com.aptana.editor.common.preferences.IPreferenceConstants.FILTER_EXPRESSIONS,
				StringUtil.join("####", filtered));
	}
}
