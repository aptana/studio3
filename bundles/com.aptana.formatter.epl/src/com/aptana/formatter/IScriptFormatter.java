/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package com.aptana.formatter;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.formatter.IFormattingContext;
import org.eclipse.text.edits.TextEdit;

import com.aptana.formatter.ui.FormatterException;

/**
 * Script source code formatter interface.
 */
public interface IScriptFormatter
{

	/**
	 * Detects the indentation level at the specified offset
	 * 
	 * @param document
	 * @param offset
	 * @param context
	 * @param isSelection
	 * @return
	 */
	int detectIndentationLevel(IDocument document, int offset, boolean isSelection, IFormattingContext context);

	/**
	 * Format <code>source</code>, and returns a text edit that correspond to the difference between the given string
	 * and the formatted string.
	 * <p>
	 * It returns null if the given string cannot be formatted.
	 * </p>
	 * 
	 * @param source
	 *            full source module content
	 * @param offset
	 *            the offset of the region to format
	 * @param length
	 *            the length of the region to format
	 * @param indentationLevel
	 *            the additional indent level
	 * @param isSelection
	 *            Indicate that we are formatting selected text.
	 * @param context
	 *            {@link IFormatterContext}
	 * @param indentSufix
	 *            Extra string indent to insert into the end of the formatter text (may be empty)
	 */
	TextEdit format(String source, int offset, int length, int indentationLevel, boolean isSelection,
			IFormattingContext context, String indentSufix) throws FormatterException;

	void setIsSlave(boolean isSlave);

	/**
	 * Returns true if this formatter is running as a 'slave' formatter.
	 * 
	 * @return boolean
	 * @see #setIsSlave(boolean)
	 */
	boolean isSlave();

	/**
	 * Returns the indentation size, as defined in the formatter preferences.
	 * 
	 * @return The indentation size.
	 */
	int getIndentSize();

	/**
	 * Returns the tab size, as defined in the formatter preferences.
	 * 
	 * @return The tab size.
	 */
	int getTabSize();

	/**
	 * Returns the tab width, as defined in the editor's specific preference page.
	 * 
	 * @return The tab width.
	 */
	int getEditorSpecificTabWidth();

	/**
	 * Returns the 'insert spaces for tabs' setting defined in the editor's specific preference page.
	 * 
	 * @return The 'insert spaces for tabs' boolean value.
	 */
	boolean isEditorInsertSpacesForTabs();

	/**
	 * Returns the indentation type, as defined in the formatter preferences.
	 * 
	 * @return The indentation type - One of {@link com.aptana.formatter.ui.CodeFormatterConstants#TAB},
	 *         {@link com.aptana.formatter.ui.CodeFormatterConstants#SPACE},
	 *         {@link com.aptana.formatter.ui.CodeFormatterConstants#MIXED} or
	 *         {@link com.aptana.formatter.ui.CodeFormatterConstants#EDITOR}
	 */
	String getIndentType();
}
