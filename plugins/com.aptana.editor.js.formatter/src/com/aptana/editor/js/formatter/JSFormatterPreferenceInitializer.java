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
package com.aptana.editor.js.formatter;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

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
		IPreferenceStore store = JSFormatterPlugin.getDefault().getPreferenceStore();
		store.setDefault(JSFormatterConstants.FORMATTER_TAB_CHAR, CodeFormatterConstants.SPACE);
		store.setDefault(JSFormatterConstants.FORMATTER_TAB_SIZE, "2"); //$NON-NLS-1$
		store.setDefault(JSFormatterConstants.FORMATTER_INDENTATION_SIZE, "2"); //$NON-NLS-1$
		store.setDefault(JSFormatterConstants.WRAP_COMMENTS, false);
		store.setDefault(JSFormatterConstants.WRAP_COMMENTS_LENGTH, 80);
		store.setDefault(JSFormatterConstants.INDENT_BLOCKS, true);
		store.setDefault(JSFormatterConstants.INDENT_SWITCH_BODY, true);
		store.setDefault(JSFormatterConstants.INDENT_CASE_BODY, true);
		store.setDefault(JSFormatterConstants.NEW_LINES_BEFORE_CATCH_STATEMENT, false);
		store.setDefault(JSFormatterConstants.NEW_LINES_BEFORE_FINALLY_STATEMENT, false);
		store.setDefault(JSFormatterConstants.NEW_LINES_BEFORE_IF_STATEMENT, false);
		store.setDefault(JSFormatterConstants.NEW_LINES_BEFORE_ELSE_STATEMENT, false);
		store.setDefault(JSFormatterConstants.NEW_LINES_BEFORE_DO_WHILE_STATEMENT, false);
		store.setDefault(JSFormatterConstants.LINES_AFTER_FUNCTION_DECLARATION, 1);
		store.setDefault(JSFormatterConstants.PRESERVED_LINES, 1);
		store.setDefault(JSFormatterConstants.BRACE_POSITION_BLOCK, CodeFormatterConstants.SAME_LINE);
		store.setDefault(JSFormatterConstants.BRACE_POSITION_BLOCK_IN_CASE, CodeFormatterConstants.SAME_LINE);
		store.setDefault(JSFormatterConstants.BRACE_POSITION_BLOCK_IN_SWITCH, CodeFormatterConstants.SAME_LINE);
		store.setDefault(JSFormatterConstants.BRACE_POSITION_FUNCTION_DECLARATION, CodeFormatterConstants.SAME_LINE);
	}
}
