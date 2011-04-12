/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.css.CSSPlugin;
import com.aptana.editor.css.parsing.ICSSParserConstants;

public class PreferenceInitializer extends AbstractPreferenceInitializer
{
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@SuppressWarnings("nls")
	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences prefs = new DefaultScope().getNode(CSSPlugin.PLUGIN_ID);

		prefs.putBoolean(com.aptana.editor.common.preferences.IPreferenceConstants.LINK_OUTLINE_WITH_EDITOR, true);
		prefs.putDouble(IPreferenceConstants.CSS_INDEX_VERSION, 0);
		prefs.putBoolean(com.aptana.editor.common.preferences.IPreferenceConstants.EDITOR_AUTO_INDENT, true);
		prefs.put(IPreferenceConstants.CSS_ACTIVATION_CHARACTERS, ".#:\t");

		prefs = new DefaultScope().getNode(CommonEditorPlugin.PLUGIN_ID);
		String[] filtered = new String[] { ".*Unknown pseudo-element.*", ".*Property _.*", ".*-moz-.*", ".*-o-*",
				".*opacity.*", ".*overflow-.*", ".*accelerator.*", ".*background-position-.*", ".*filter.*",
				".*ime-mode.*", ".*layout-.*", ".*line-break.*", ".*page.*", ".*ruby-.*", ".*scrollbar-.*",
				".*text-align-.*", ".*text-justify.*", ".*text-overflow.*", ".*text-shadow.*",
				".*text-underline-position.*", ".*word-spacing.*", ".*word-wrap.*", ".*writing-mode.*", ".*zoom.*",
				".*Parse Error.*", ".*-webkit-.*", ".*rgba.*is not a background.*" };
		prefs.put(ICSSParserConstants.LANGUAGE + ":"
				+ com.aptana.editor.common.preferences.IPreferenceConstants.FILTER_EXPRESSIONS,
				StringUtil.join("####", filtered));
	}

}
