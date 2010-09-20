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
	public static final String FORMATTER_INDENTATION_SIZE = FORMATTER_ID + '.'
			+ CodeFormatterConstants.FORMATTER_INDENTATION_SIZE;
}
