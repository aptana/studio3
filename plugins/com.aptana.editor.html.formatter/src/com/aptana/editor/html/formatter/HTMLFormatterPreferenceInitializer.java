package com.aptana.editor.html.formatter;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.aptana.formatter.ui.CodeFormatterConstants;
import com.aptana.ui.preferences.IPreferenceDelegate;

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
		// We add all the 'Void' html tags here as well. They should not trigger an indent increase.
		store.setDefault(HTMLFormatterConstants.INDENT_EXCLUDED_TAGS,
				"br,a,i,b,em,strong,h1,h2,h3,h4,h5,h6,area,base,col,command,embed,hr,img,input,keygen,link,meta,param,source,track,wbr" //$NON-NLS-1$
				.replaceAll(",", IPreferenceDelegate.PREFERECE_DELIMITER)); //$NON-NLS-1$
		store.setDefault(HTMLFormatterConstants.NEW_LINES_EXCLUDED_TAGS,
				"a,span,i,b,em,strong,h1,h2,h3,h4,h5,h6,title,option,meta".replaceAll(",", //$NON-NLS-1$//$NON-NLS-2$
						IPreferenceDelegate.PREFERECE_DELIMITER));
		store.setDefault(HTMLFormatterConstants.LINES_AFTER_ELEMENTS, 1);
		store.setDefault(HTMLFormatterConstants.LINES_AFTER_NON_HTML_ELEMENTS, 1);
		store.setDefault(HTMLFormatterConstants.LINES_BEFORE_NON_HTML_ELEMENTS, 1);
		store.setDefault(HTMLFormatterConstants.PRESERVED_LINES, 1);
	}
}
