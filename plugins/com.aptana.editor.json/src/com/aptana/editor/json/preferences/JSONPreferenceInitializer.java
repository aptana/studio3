/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.json.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.editor.common.util.EditorUtil;
import com.aptana.editor.json.JSONPlugin;
import com.aptana.formatter.ui.CodeFormatterConstants;

/**
 * JSONPreferenceInitializer
 */
public class JSONPreferenceInitializer extends AbstractPreferenceInitializer
{
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences pref = DefaultScope.INSTANCE.getNode(JSONPlugin.PLUGIN_ID);

		pref.put(IPreferenceConstants.FORMATTER_TAB_CHAR, CodeFormatterConstants.EDITOR);
		pref.put(IPreferenceConstants.FORMATTER_TAB_SIZE,
				Integer.toString(EditorUtil.getSpaceIndentSize(JSONPlugin.getDefault().getBundle().getSymbolicName())));
		pref.put(IPreferenceConstants.FORMATTER_INDENTATION_SIZE, "4"); //$NON-NLS-1$
		pref.putBoolean(com.aptana.editor.common.preferences.IPreferenceConstants.EDITOR_AUTO_INDENT, true);
		pref.putBoolean(com.aptana.editor.common.preferences.IPreferenceConstants.EDITOR_ENABLE_FOLDING, true);

		// mark occurrences
		// pref.putBoolean(com.aptana.editor.common.preferences.IPreferenceConstants.EDITOR_MARK_OCCURRENCES, true);
	}
}
