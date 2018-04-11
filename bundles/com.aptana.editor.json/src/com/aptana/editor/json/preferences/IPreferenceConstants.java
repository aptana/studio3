/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.json.preferences;

import com.aptana.formatter.ui.CodeFormatterConstants;

/**
 * IPreferenceConstants
 */
public interface IPreferenceConstants
{
	public static final String FORMATTER_ID = "json.formatter"; //$NON-NLS-1$

	// tabs
	public static final String FORMATTER_TAB_CHAR = FORMATTER_ID + "." + CodeFormatterConstants.FORMATTER_TAB_CHAR; //$NON-NLS-1$
	public static final String FORMATTER_TAB_SIZE = FORMATTER_ID + "." + CodeFormatterConstants.FORMATTER_TAB_SIZE; //$NON-NLS-1$

	// indentation
	public static final String FORMATTER_INDENTATION_SIZE = FORMATTER_ID
			+ "." + CodeFormatterConstants.FORMATTER_INDENTATION_SIZE; //$NON-NLS-1$

	// wrapping
	public static final String WRAP_COMMENTS = FORMATTER_ID + ".wrap.comments"; //$NON-NLS-1$
	public static final String WRAP_COMMENTS_LENGTH = FORMATTER_ID + ".wrap.comments.length"; //$NON-NLS-1$

	/**
	 * A pref key used to determine if objects are initially folded. Value is a boolean.
	 */
	public static final String INITIALLY_FOLD_OBJECTS = "fold_objects"; //$NON-NLS-1$

	/**
	 * A pref key used to determine if arrays are initially folded. Value is a boolean.
	 */
	public static final String INITIALLY_FOLD_ARRAYS = "fold_arrays"; //$NON-NLS-1$

}
