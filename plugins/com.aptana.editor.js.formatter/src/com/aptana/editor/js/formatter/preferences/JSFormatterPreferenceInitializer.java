/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.formatter.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.editor.js.formatter.JSFormatterConstants;
import com.aptana.editor.js.formatter.JSFormatterPlugin;
import com.aptana.formatter.epl.FormatterPlugin;
import com.aptana.formatter.ui.CodeFormatterConstants;

/**
 * JavaScript formatter preference initializer.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class JSFormatterPreferenceInitializer extends AbstractPreferenceInitializer
{

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences store = new DefaultScope().getNode(JSFormatterPlugin.PLUGIN_ID);

		store.put(JSFormatterConstants.FORMATTER_TAB_CHAR, CodeFormatterConstants.EDITOR);
		store.put(JSFormatterConstants.FORMATTER_TAB_SIZE, "4"); //$NON-NLS-1$
		store.put(JSFormatterConstants.FORMATTER_INDENTATION_SIZE, "4"); //$NON-NLS-1$
		store.putBoolean(JSFormatterConstants.WRAP_COMMENTS, false);
		store.putInt(JSFormatterConstants.WRAP_COMMENTS_LENGTH, 80);
		store.putBoolean(JSFormatterConstants.INDENT_BLOCKS, true);
		store.putBoolean(JSFormatterConstants.INDENT_FUNCTION_BODY, true);
		store.putBoolean(JSFormatterConstants.INDENT_SWITCH_BODY, true);
		store.putBoolean(JSFormatterConstants.INDENT_CASE_BODY, true);
		store.putBoolean(JSFormatterConstants.INDENT_GROUP_BODY, true);
		store.putBoolean(JSFormatterConstants.NEW_LINES_BEFORE_CATCH_STATEMENT, false);
		store.putBoolean(JSFormatterConstants.NEW_LINES_BEFORE_FINALLY_STATEMENT, false);
		store.putBoolean(JSFormatterConstants.NEW_LINES_BEFORE_ELSE_STATEMENT, false);
		store.putBoolean(JSFormatterConstants.NEW_LINES_BEFORE_IF_IN_ELSEIF_STATEMENT, false);
		store.putBoolean(JSFormatterConstants.NEW_LINES_BEFORE_DO_WHILE_STATEMENT, false);
		store.putInt(JSFormatterConstants.LINES_AFTER_FUNCTION_DECLARATION, 1);
		store.putInt(JSFormatterConstants.LINES_AFTER_FUNCTION_DECLARATION_IN_EXPRESSION, 0);
		store.putInt(JSFormatterConstants.PRESERVED_LINES, 1);
		store.put(JSFormatterConstants.BRACE_POSITION_BLOCK, CodeFormatterConstants.SAME_LINE);
		store.put(JSFormatterConstants.BRACE_POSITION_BLOCK_IN_CASE, CodeFormatterConstants.SAME_LINE);
		store.put(JSFormatterConstants.BRACE_POSITION_BLOCK_IN_SWITCH, CodeFormatterConstants.SAME_LINE);
		store.put(JSFormatterConstants.BRACE_POSITION_FUNCTION_DECLARATION, CodeFormatterConstants.SAME_LINE);
		try
		{
			store.flush();
		}
		catch (BackingStoreException e)
		{
			FormatterPlugin.logError(e);
		}
	}
}
