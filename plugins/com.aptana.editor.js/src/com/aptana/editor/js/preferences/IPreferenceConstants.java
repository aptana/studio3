/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.preferences;

public interface IPreferenceConstants
{
	/**
	 * The value is a boolean to indicate if "*" should be used for multiline comment indenting
	 */
	String COMMENT_INDENT_USE_STAR = "COMMENT_INDENT_USE_STAR"; //$NON-NLS-1$s

	/**
	 * A pref key used to determine if comments are initially folded. Value is a boolean.
	 */
	String INITIALLY_FOLD_COMMENTS = "fold_comments"; //$NON-NLS-1$

	/**
	 * A pref key used to determine if functions are initially folded. Value is a boolean.
	 */
	String INITIALLY_FOLD_FUNCTIONS = "fold_functions"; //$NON-NLS-1$

	/**
	 * A pref key used to determine if objects are initially folded. Value is a boolean.
	 */
	String INITIALLY_FOLD_OBJECTS = "fold_objects"; //$NON-NLS-1$

	/**
	 * A pref key used to determine if arrays are initially folded. Value is a boolean.
	 */
	String INITIALLY_FOLD_ARRAYS = "fold_arrays"; //$NON-NLS-1$

}
