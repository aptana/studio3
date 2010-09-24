/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package com.aptana.editor.ruby.formatter.internal;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.aptana.editor.ruby.formatter.RubyFormatterConstants;
import com.aptana.editor.ruby.formatter.RubyFormatterPlugin;
import com.aptana.formatter.ui.CodeFormatterConstants;

public class RubyFormatterPreferenceInitializer extends AbstractPreferenceInitializer
{

	public void initializeDefaultPreferences()
	{
		IPreferenceStore store = RubyFormatterPlugin.getDefault().getPreferenceStore();
		//
		store.setDefault(RubyFormatterConstants.INDENT_CLASS, true);
		store.setDefault(RubyFormatterConstants.INDENT_MODULE, true);
		store.setDefault(RubyFormatterConstants.INDENT_METHOD, true);
		store.setDefault(RubyFormatterConstants.INDENT_BLOCKS, true);
		store.setDefault(RubyFormatterConstants.INDENT_CASE, false);
		store.setDefault(RubyFormatterConstants.INDENT_WHEN, true);
		store.setDefault(RubyFormatterConstants.INDENT_IF, true);
		//		
		store.setDefault(RubyFormatterConstants.LINES_FILE_AFTER_REQUIRE, 1);
		//
		store.setDefault(RubyFormatterConstants.LINES_FILE_BETWEEN_MODULE, 1);
		store.setDefault(RubyFormatterConstants.LINES_FILE_BETWEEN_CLASS, 1);
		store.setDefault(RubyFormatterConstants.LINES_FILE_BETWEEN_METHOD, 1);
		//
		store.setDefault(RubyFormatterConstants.LINES_BEFORE_FIRST, 0);
		store.setDefault(RubyFormatterConstants.LINES_BEFORE_MODULE, 1);
		store.setDefault(RubyFormatterConstants.LINES_BEFORE_CLASS, 1);
		store.setDefault(RubyFormatterConstants.LINES_BEFORE_METHOD, 1);
		//
		store.setDefault(RubyFormatterConstants.LINES_PRESERVE, 1);
		//
		store.setDefault(RubyFormatterConstants.WRAP_COMMENTS, false);
		store.setDefault(RubyFormatterConstants.WRAP_COMMENTS_LENGTH, 80);

		store.setDefault(RubyFormatterConstants.FORMATTER_TAB_CHAR, CodeFormatterConstants.SPACE);
		store.setDefault(RubyFormatterConstants.FORMATTER_TAB_SIZE, "2"); //$NON-NLS-1$
		store.setDefault(RubyFormatterConstants.FORMATTER_INDENTATION_SIZE, "2"); //$NON-NLS-1$
	}
}
