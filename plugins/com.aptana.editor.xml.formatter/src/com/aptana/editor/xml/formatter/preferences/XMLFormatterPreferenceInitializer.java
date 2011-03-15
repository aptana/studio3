/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.formatter.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.xml.formatter.XMLFormatterConstants;
import com.aptana.editor.xml.formatter.XMLFormatterPlugin;
import com.aptana.formatter.epl.FormatterPlugin;
import com.aptana.formatter.ui.CodeFormatterConstants;

public class XMLFormatterPreferenceInitializer extends AbstractPreferenceInitializer
{

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences store = new DefaultScope().getNode(XMLFormatterPlugin.PLUGIN_ID);
		store.put(XMLFormatterConstants.FORMATTER_TAB_CHAR, CodeFormatterConstants.EDITOR);
		store.put(XMLFormatterConstants.FORMATTER_TAB_SIZE, "4"); //$NON-NLS-1$
		store.put(XMLFormatterConstants.FORMATTER_INDENTATION_SIZE, "4"); //$NON-NLS-1$
		store.putBoolean(XMLFormatterConstants.WRAP_COMMENTS, false);
		store.putInt(XMLFormatterConstants.WRAP_COMMENTS_LENGTH, 80);
		// No excluded tags by default
		store.put(XMLFormatterConstants.INDENT_EXCLUDED_TAGS, StringUtil.EMPTY);
		store.put(XMLFormatterConstants.NEW_LINES_EXCLUDED_TAGS, StringUtil.EMPTY);
		store.putInt(XMLFormatterConstants.LINES_AFTER_ELEMENTS, 0);
		store.putInt(XMLFormatterConstants.LINES_AFTER_NON_XML_ELEMENTS, 1);
		store.putInt(XMLFormatterConstants.LINES_BEFORE_NON_XML_ELEMENTS, 1);
		store.putInt(XMLFormatterConstants.PRESERVED_LINES, 1);

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
