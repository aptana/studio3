/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.formatter;

import com.aptana.formatter.ui.CodeFormatterConstants;

/**
 * CSS code formatter constants.<br>
 * Since the formatters will be saved in a unified XML file, it's important to have a unique key for every setting. The
 * CSS formatter constants are all starting with the {@link #FORMATTER_ID} string.
 */
public interface CSSFormatterConstants
{

	/**
	 * CSS formatter ID.
	 */
	public static final String FORMATTER_ID = "css.formatter"; //$NON-NLS-1$

	public static final String FORMATTER_TAB_CHAR = FORMATTER_ID + '.' + CodeFormatterConstants.FORMATTER_TAB_CHAR;
	public static final String FORMATTER_TAB_SIZE = FORMATTER_ID + '.' + CodeFormatterConstants.FORMATTER_TAB_SIZE;

	// Wrapping
	public static final String WRAP_COMMENTS = FORMATTER_ID + ".wrap.comments"; //$NON-NLS-1$
	public static final String WRAP_COMMENTS_LENGTH = FORMATTER_ID + ".wrap.comments.length"; //$NON-NLS-1$

	// Indentation

	public static final String FORMATTER_INDENTATION_SIZE = FORMATTER_ID + '.'
			+ CodeFormatterConstants.FORMATTER_INDENTATION_SIZE;

	// New lines
	public static final String NEW_LINES_BEFORE_BLOCKS = FORMATTER_ID + ".indent.blocks"; //$NON-NLS-1$

	// Empty lines
	public static final String LINES_AFTER_ELEMENTS = FORMATTER_ID + ".line.after.element"; //$NON-NLS-1$
	public static final String LINES_AFTER_DECLARATION = FORMATTER_ID + ".line.after.declaration"; //$NON-NLS-1$
	public static final String PRESERVED_LINES = FORMATTER_ID + ".line.preserve"; //$NON-NLS-1$

	// Spaces
	public static final String SPACES_BEFORE_COMMAS = FORMATTER_ID + ".spaces.before.commas"; //$NON-NLS-1$
	public static final String SPACES_AFTER_COMMAS = FORMATTER_ID + ".spaces.after.commas"; //$NON-NLS-1$
	public static final String SPACES_BEFORE_PARENTHESES = FORMATTER_ID + ".spaces.before.parentheses"; //$NON-NLS-1$
	public static final String SPACES_AFTER_PARENTHESES = FORMATTER_ID + ".spaces.after.parentheses"; //$NON-NLS-1$
	public static final String SPACES_BEFORE_COLON = FORMATTER_ID + ".spaces.before.colon"; //$NON-NLS-1$
	public static final String SPACES_AFTER_COLON = FORMATTER_ID + ".spaces.after.colon"; //$NON-NLS-1$
	public static final String SPACES_BEFORE_SEMICOLON = FORMATTER_ID + ".spaces.before.semicolon"; //$NON-NLS-1$
	public static final String SPACES_AFTER_SEMICOLON = FORMATTER_ID + ".spaces.after.semicolon"; //$NON-NLS-1$
	public static final String SPACES_BEFORE_CHILD_COMBINATOR = FORMATTER_ID + ".spaces.before.child.combinator"; //$NON-NLS-1$
	public static final String SPACES_AFTER_CHILD_COMBINATOR = FORMATTER_ID + ".spaces.after.child.combinator"; //$NON-NLS-1$

	// OFF/ON
	public static final String FORMATTER_OFF_ON_ENABLED = FORMATTER_ID + ".formatter.on.off.enabled"; //$NON-NLS-1$
	public static final String FORMATTER_ON = FORMATTER_ID + ".formatter.on"; //$NON-NLS-1$
	public static final String FORMATTER_OFF = FORMATTER_ID + ".formatter.off"; //$NON-NLS-1$
	public static final String DEFAULT_FORMATTER_OFF = "@formatter:off"; //$NON-NLS-1$
	public static final String DEFAULT_FORMATTER_ON = "@formatter:on"; //$NON-NLS-1$
}
