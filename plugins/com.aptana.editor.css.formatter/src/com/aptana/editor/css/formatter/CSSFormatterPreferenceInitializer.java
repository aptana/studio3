package com.aptana.editor.css.formatter;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.aptana.formatter.ui.CodeFormatterConstants;
import com.aptana.ui.preferences.IPreferenceDelegate;

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
		// We add all the 'Void' html tags here as well. They should not trigger an indent increase.
		store.setDefault(CSSFormatterConstants.INDENT_EXCLUDED_TAGS,
				"br,a,i,b,em,strong,h1,h2,h3,h4,h5,h6,area,base,col,command,embed,hr,img,input,keygen,link,meta,param,source,track,wbr,td,th" //$NON-NLS-1$
				.replaceAll(",", IPreferenceDelegate.PREFERECE_DELIMITER)); //$NON-NLS-1$
		store.setDefault(CSSFormatterConstants.NEW_LINES_EXCLUDED_TAGS,
				"a,span,i,b,em,strong,h1,h2,h3,h4,h5,h6,title,option,meta,td,th".replaceAll(",", //$NON-NLS-1$//$NON-NLS-2$
						IPreferenceDelegate.PREFERECE_DELIMITER));
		store.setDefault(CSSFormatterConstants.LINES_AFTER_ELEMENTS, 0);
		store.setDefault(CSSFormatterConstants.LINES_BEFORE_NON_CSS_ELEMENTS, 1);
		store.setDefault(CSSFormatterConstants.LINES_AFTER_NON_CSS_ELEMENTS, 1);
		store.setDefault(CSSFormatterConstants.PRESERVED_LINES, 1);
	}
}

