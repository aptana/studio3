/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
		store.put(HTMLFormatterConstants.FORMATTER_TAB_CHAR, CodeFormatterConstants.SPACE);
		store.put(HTMLFormatterConstants.FORMATTER_TAB_SIZE, "4"); //$NON-NLS-1$
		store.put(HTMLFormatterConstants.FORMATTER_INDENTATION_SIZE, "4"); //$NON-NLS-1$
		store.putBoolean(HTMLFormatterConstants.WRAP_COMMENTS, false);
		store.putInt(HTMLFormatterConstants.WRAP_COMMENTS_LENGTH, 80);
		// We add all the 'Void' html tags here as well. They should not trigger an indent increase.
		store
				.put(
						HTMLFormatterConstants.INDENT_EXCLUDED_TAGS,
						"br,a,i,b,em,strong,h1,h2,h3,h4,h5,h6,area,base,col,command,embed,hr,img,input,keygen,link,meta,param,source,track,wbr,td,th" //$NON-NLS-1$
						.replaceAll(",", IPreferenceDelegate.PREFERECE_DELIMITER)); //$NON-NLS-1$
		store.put(HTMLFormatterConstants.NEW_LINES_EXCLUDED_TAGS,
				"a,span,i,b,em,strong,h1,h2,h3,h4,h5,h6,title,option,meta,td,th".replaceAll(",", //$NON-NLS-1$//$NON-NLS-2$
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
