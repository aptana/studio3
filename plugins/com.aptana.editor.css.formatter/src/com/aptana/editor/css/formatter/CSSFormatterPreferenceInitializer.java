package com.aptana.editor.css.formatter;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

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
		IPreferenceStore store = CSSFormatterPlugin.getDefault().getPreferenceStore();
		store.setDefault(CSSFormatterConstants.FORMATTER_TAB_CHAR, CodeFormatterConstants.SPACE);
		store.setDefault(CSSFormatterConstants.FORMATTER_TAB_SIZE, "2"); //$NON-NLS-1$
		store.setDefault(CSSFormatterConstants.FORMATTER_INDENTATION_SIZE, "2"); //$NON-NLS-1$
		store.setDefault(CSSFormatterConstants.WRAP_COMMENTS, true);
		store.setDefault(CSSFormatterConstants.WRAP_COMMENTS_LENGTH, 10);
		store.setDefault(CSSFormatterConstants.NEW_LINES_BEFORE_BLOCKS, false);
		store.setDefault(CSSFormatterConstants.LINES_AFTER_ELEMENTS, 0);
		store.setDefault(CSSFormatterConstants.PRESERVED_LINES, 1);
	}
}

