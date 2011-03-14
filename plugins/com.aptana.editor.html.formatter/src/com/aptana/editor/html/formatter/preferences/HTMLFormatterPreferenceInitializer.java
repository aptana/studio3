/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.formatter.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.editor.html.formatter.HTMLFormatterConstants;
import com.aptana.editor.html.formatter.HTMLFormatterPlugin;
import com.aptana.formatter.epl.FormatterPlugin;
import com.aptana.formatter.preferences.IPreferenceDelegate;
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
		IEclipsePreferences store = new DefaultScope().getNode(HTMLFormatterPlugin.PLUGIN_ID);
		store.put(HTMLFormatterConstants.FORMATTER_TAB_CHAR, CodeFormatterConstants.EDITOR);
		store.put(HTMLFormatterConstants.FORMATTER_TAB_SIZE, "4"); //$NON-NLS-1$
		store.put(HTMLFormatterConstants.FORMATTER_INDENTATION_SIZE, "4"); //$NON-NLS-1$
		store.putBoolean(HTMLFormatterConstants.WRAP_COMMENTS, false);
		store.putInt(HTMLFormatterConstants.WRAP_COMMENTS_LENGTH, 80);
		// We add all the 'Void' html tags here as well. They should not trigger an indent increase.
		store
				.put(
						HTMLFormatterConstants.INDENT_EXCLUDED_TAGS,
						"br,a,i,b,em,strong,h1,h2,h3,h4,h5,h6,area,base,col,command,embed,hr,img,input,keygen,link,param,source,track,wbr,td,th" //$NON-NLS-1$
						.replaceAll(",", IPreferenceDelegate.PREFERECE_DELIMITER)); //$NON-NLS-1$
		store.put(HTMLFormatterConstants.NEW_LINES_EXCLUDED_TAGS,
				"a,span,i,b,em,strong,h1,h2,h3,h4,h5,h6,title,option,td,th".replaceAll(",", //$NON-NLS-1$//$NON-NLS-2$
						IPreferenceDelegate.PREFERECE_DELIMITER));
		store.putInt(HTMLFormatterConstants.LINES_AFTER_ELEMENTS, 0);
		store.putInt(HTMLFormatterConstants.LINES_AFTER_NON_HTML_ELEMENTS, 1);
		store.putInt(HTMLFormatterConstants.LINES_BEFORE_NON_HTML_ELEMENTS, 1);
		store.putInt(HTMLFormatterConstants.PRESERVED_LINES, 1);

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
