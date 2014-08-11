/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.formatter.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.common.util.EditorUtil;
import com.aptana.editor.css.CSSPlugin;
import com.aptana.editor.css.formatter.CSSFormatterConstants;
import com.aptana.editor.css.formatter.CSSFormatterPlugin;
import com.aptana.formatter.IDebugScopes;
import com.aptana.formatter.ui.CodeFormatterConstants;

public class CSSFormatterPreferenceInitializer extends AbstractPreferenceInitializer
{

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences pref = DefaultScope.INSTANCE.getNode(CSSFormatterPlugin.PLUGIN_ID);

		pref.put(CSSFormatterConstants.FORMATTER_TAB_CHAR, CodeFormatterConstants.EDITOR);
		pref.put(CSSFormatterConstants.FORMATTER_TAB_SIZE,
				Integer.toString(EditorUtil.getSpaceIndentSize(CSSPlugin.getDefault().getBundle().getSymbolicName())));
		pref.put(CSSFormatterConstants.FORMATTER_INDENTATION_SIZE, "4"); //$NON-NLS-1$
		pref.putBoolean(CSSFormatterConstants.WRAP_COMMENTS, false);
		pref.putInt(CSSFormatterConstants.WRAP_COMMENTS_LENGTH, 80);
		pref.put(CSSFormatterConstants.NEW_LINES_BEFORE_BLOCKS, CodeFormatterConstants.SAME_LINE);
		pref.putInt(CSSFormatterConstants.LINES_AFTER_ELEMENTS, 0);
		pref.putInt(CSSFormatterConstants.LINES_AFTER_DECLARATION, 0);
		pref.putInt(CSSFormatterConstants.PRESERVED_LINES, 1);

		pref.putInt(CSSFormatterConstants.SPACES_BEFORE_COMMAS, 0);
		pref.putInt(CSSFormatterConstants.SPACES_AFTER_COMMAS, 1);
		pref.putInt(CSSFormatterConstants.SPACES_BEFORE_SEMICOLON, 0);
		pref.putInt(CSSFormatterConstants.SPACES_AFTER_SEMICOLON, 0);
		pref.putInt(CSSFormatterConstants.SPACES_BEFORE_PARENTHESES, 1);
		pref.putInt(CSSFormatterConstants.SPACES_AFTER_PARENTHESES, 1);
		pref.putInt(CSSFormatterConstants.SPACES_BEFORE_COLON, 0);
		pref.putInt(CSSFormatterConstants.SPACES_AFTER_COLON, 1);
		pref.putInt(CSSFormatterConstants.SPACES_BEFORE_CHILD_COMBINATOR, 1);
		pref.putInt(CSSFormatterConstants.SPACES_AFTER_CHILD_COMBINATOR, 1);

		pref.putBoolean(CSSFormatterConstants.FORMATTER_OFF_ON_ENABLED, false);
		pref.put(CSSFormatterConstants.FORMATTER_ON, CSSFormatterConstants.DEFAULT_FORMATTER_ON);
		pref.put(CSSFormatterConstants.FORMATTER_OFF, CSSFormatterConstants.DEFAULT_FORMATTER_OFF);

		try
		{
			pref.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(CSSFormatterPlugin.getDefault(), e, IDebugScopes.DEBUG);
		}
	}
}
