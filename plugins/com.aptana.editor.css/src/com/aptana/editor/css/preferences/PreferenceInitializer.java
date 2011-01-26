/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;

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
		prefs.put(IPreferenceConstants.CSS_ACTIVATION_CHARACTERS, ".#:\t"); //$NON-NLS-1$
		prefs.putInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH, 4);

		prefs = new DefaultScope().getNode(CommonEditorPlugin.PLUGIN_ID);
		String[] filtered = new String[] { ".*Unknown pseudo-element.*", ".*Property _.*", ".*-moz-.*", ".*-o-*", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				".*opacity.*", ".*overflow-.*", ".*accelerator.*", ".*background-position-.*", ".*filter.*", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				".*ime-mode.*", ".*layout-.*", ".*line-break.*", ".*page.*", ".*ruby-.*", ".*scrollbar-.*", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
				".*text-align-.*", ".*text-justify.*", ".*text-overflow.*", ".*text-shadow.*", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				".*text-underline-position.*", ".*word-spacing.*", ".*word-wrap.*", ".*writing-mode.*", ".*zoom.*", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				".*Parse Error.*", ".*-webkit-.*" }; //$NON-NLS-1$ //$NON-NLS-2$
		prefs.put(ICSSParserConstants.LANGUAGE + ":" //$NON-NLS-1$
				+ com.aptana.editor.common.preferences.IPreferenceConstants.FILTER_EXPRESSIONS,
				StringUtil.join("####", filtered)); //$NON-NLS-1$

	}

}
