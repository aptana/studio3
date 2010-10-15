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
	public static final String NEW_LINES_BEFORE_BLOCKS = FORMATTER_ID + ".indent.blocks";
	
	// Empty lines
	public static final String LINES_AFTER_ELEMENTS = FORMATTER_ID + ".line.after.element"; //$NON-NLS-1$
	public static final String PRESERVED_LINES = FORMATTER_ID + ".line.preserve"; //$NON-NLS-1$

	
}
