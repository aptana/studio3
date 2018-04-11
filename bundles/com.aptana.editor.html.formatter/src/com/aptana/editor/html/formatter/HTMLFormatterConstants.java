/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.formatter;

import com.aptana.formatter.ui.CodeFormatterConstants;

/**
 * HTML code formatter constants.<br>
 * Since the formatters will be saved in a unified XML file, it's important to have a unique key for every setting. The
 * HTML formatter constants are all starting with the {@link #FORMATTER_ID} string.
 */
public interface HTMLFormatterConstants
{

	/**
	 * HTML formatter ID.
	 */
	public static final String FORMATTER_ID = "html.formatter"; //$NON-NLS-1$

	public static final String FORMATTER_TAB_CHAR = FORMATTER_ID + '.' + CodeFormatterConstants.FORMATTER_TAB_CHAR;
	public static final String FORMATTER_TAB_SIZE = FORMATTER_ID + '.' + CodeFormatterConstants.FORMATTER_TAB_SIZE;

	// Wrapping
	public static final String WRAP_COMMENTS = FORMATTER_ID + ".wrap.comments"; //$NON-NLS-1$
	public static final String WRAP_COMMENTS_LENGTH = FORMATTER_ID + ".wrap.comments.length"; //$NON-NLS-1$

	// Indentation
	public static final String INDENT_EXCLUDED_TAGS = FORMATTER_ID + ".indent.excluded"; //$NON-NLS-1$

	public static final String FORMATTER_INDENTATION_SIZE = FORMATTER_ID + '.'
			+ CodeFormatterConstants.FORMATTER_INDENTATION_SIZE;

	// New lines
	public static final String NEW_LINES_EXCLUDED_TAGS = FORMATTER_ID + ".newline.excluded"; //$NON-NLS-1$
	public static final String NEW_LINES_EXCLUSION_IN_EMPTY_TAGS = FORMATTER_ID + ".newline.exclusion.in.empty.tag"; //$NON-NLS-1$

	// Empty lines
	public static final String LINES_AFTER_ELEMENTS = FORMATTER_ID + ".line.after.element"; //$NON-NLS-1$
	public static final String LINES_BEFORE_NON_HTML_ELEMENTS = FORMATTER_ID + ".line.before.non.html"; //$NON-NLS-1$
	public static final String LINES_AFTER_NON_HTML_ELEMENTS = FORMATTER_ID + ".line.after.non.html"; //$NON-NLS-1$
	public static final String PRESERVED_LINES = FORMATTER_ID + ".line.preserve"; //$NON-NLS-1$

	// Spaces
	public static final String TRIM_SPACES = FORMATTER_ID + ".spaces.trim"; //$NON-NLS-1$

	// Comments
	public static final String PLACE_COMMENTS_IN_SEPARATE_LINES = FORMATTER_ID + ".comments.in.separate.lines"; //$NON-NLS-1$

	// OFF/ON
	public static final String FORMATTER_OFF_ON_ENABLED = FORMATTER_ID + ".formatter.on.off.enabled"; //$NON-NLS-1$
	public static final String FORMATTER_ON = FORMATTER_ID + ".formatter.on"; //$NON-NLS-1$
	public static final String FORMATTER_OFF = FORMATTER_ID + ".formatter.off"; //$NON-NLS-1$
	public static final String DEFAULT_FORMATTER_OFF = "@formatter:off"; //$NON-NLS-1$
	public static final String DEFAULT_FORMATTER_ON = "@formatter:on"; //$NON-NLS-1$
}
