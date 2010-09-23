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
	public static final String FORMATTER_INDENTATION_SIZE = FORMATTER_ID + '.'
			+ CodeFormatterConstants.FORMATTER_INDENTATION_SIZE;
	public static final String INDENT_HTML = FORMATTER_ID + ".indent.html"; //$NON-NLS-1$
	public static final String INDENT_HEAD = FORMATTER_ID + ".indent.head"; //$NON-NLS-1$
	public static final String INDENT_BODY = FORMATTER_ID + ".indent.body"; //$NON-NLS-1$
	public static final String INDENT_META = FORMATTER_ID + ".indent.meta"; //$NON-NLS-1$
	public static final String INDENT_TABLE = FORMATTER_ID + ".indent.table"; //$NON-NLS-1$
	public static final String INDENT_TABLE_TD = FORMATTER_ID + ".indent.table.td"; //$NON-NLS-1$
	public static final String INDENT_TABLE_TR = FORMATTER_ID + ".indent.table.tr"; //$NON-NLS-1$
	public static final String INDENT_TABLE_TH = FORMATTER_ID + ".indent.table.th"; //$NON-NLS-1$
	public static final String INDENT_UL = FORMATTER_ID + ".indent.ul"; //$NON-NLS-1$
	public static final String INDENT_LI = FORMATTER_ID + ".indent.li"; //$NON-NLS-1$

	// Blank lines
	public static final String LINES_AFTER_ELEMENTS = FORMATTER_ID + ".line.after.element"; //$NON-NLS-1$
	public static final String LINES_BEFORE_NON_HTML_ELEMENTS = FORMATTER_ID + ".line.before.non.html"; //$NON-NLS-1$
	public static final String LINES_AFTER_NON_HTML_ELEMENTS = FORMATTER_ID + ".line.after.non.html"; //$NON-NLS-1$
	public static final String PRESERVED_LINES = FORMATTER_ID + ".line.preserve"; //$NON-NLS-1$
}
