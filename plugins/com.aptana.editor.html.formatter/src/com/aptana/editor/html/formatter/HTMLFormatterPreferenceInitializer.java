package com.aptana.editor.html.formatter;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.aptana.formatter.ui.CodeFormatterConstants;

/**
 * HTML formatter preference initializer.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class HTMLFormatterPreferenceInitializer extends AbstractPreferenceInitializer
{

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences()
	{
		IPreferenceStore store = HTMLFormatterPlugin.getDefault().getPreferenceStore();
		store.setDefault(HTMLFormatterConstants.FORMATTER_TAB_CHAR, CodeFormatterConstants.SPACE);
		store.setDefault(HTMLFormatterConstants.FORMATTER_TAB_SIZE, "2"); //$NON-NLS-1$
		store.setDefault(HTMLFormatterConstants.FORMATTER_INDENTATION_SIZE, "2"); //$NON-NLS-1$
		store.setDefault(HTMLFormatterConstants.WRAP_COMMENTS, false);
		store.setDefault(HTMLFormatterConstants.WRAP_COMMENTS_LENGTH, 80);
		store.setDefault(HTMLFormatterConstants.INDENT_HTML, true);
		store.setDefault(HTMLFormatterConstants.INDENT_HEAD, true);
		store.setDefault(HTMLFormatterConstants.INDENT_BODY, true);
		store.setDefault(HTMLFormatterConstants.INDENT_META, true);
		store.setDefault(HTMLFormatterConstants.INDENT_TABLE, true);
		store.setDefault(HTMLFormatterConstants.INDENT_TABLE_TD, true);
		store.setDefault(HTMLFormatterConstants.INDENT_TABLE_TR, true);
		store.setDefault(HTMLFormatterConstants.INDENT_TABLE_TH, true);
		store.setDefault(HTMLFormatterConstants.INDENT_UL, true);
		store.setDefault(HTMLFormatterConstants.INDENT_LI, true);
		store.setDefault(HTMLFormatterConstants.LINES_AFTER_ELEMENTS, 1);
		store.setDefault(HTMLFormatterConstants.LINES_AFTER_NON_HTML_ELEMENTS, 1);
		store.setDefault(HTMLFormatterConstants.LINES_BEFORE_NON_HTML_ELEMENTS, 1);
		store.setDefault(HTMLFormatterConstants.PRESERVED_LINES, 1);
	}
}
