/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core;

import java.text.MessageFormat;
import java.util.regex.Pattern;

import com.aptana.js.core.inferencing.JSTypeUtil;

public class JSTypeConstants
{
	public static final String ARRAY_TYPE = "Array"; //$NON-NLS-1$
	public static final String BOOLEAN_TYPE = "Boolean"; //$NON-NLS-1$
	public static final String CLASS_TYPE = "Class"; //$NON-NLS-1$
	public static final String FUNCTION_TYPE = "Function"; //$NON-NLS-1$
	public static final String GLOBAL_TYPE = "Global"; //$NON-NLS-1$
	public static final String NUMBER_TYPE = "Number"; //$NON-NLS-1$
	public static final String OBJECT_TYPE = "Object"; //$NON-NLS-1$
	public static final String REG_EXP_TYPE = "RegExp"; //$NON-NLS-1$
	public static final String STRING_TYPE = "String"; //$NON-NLS-1$
	public static final String WINDOW_TYPE = "Window"; //$NON-NLS-1$
	public static final String USER_TYPE = "UserType"; //$NON-NLS-1$

	/**
	 * jQuery specific constants.
	 */
	public static final String JQUERY = "jQuery"; //$NON-NLS-1$
	public static final String FUNCTION_JQUERY = JSTypeUtil.toFunctionType(JQUERY);
	public static final String CLASS_JQUERY = JSTypeUtil.toClassType(JQUERY);
	public static final String DOLLAR = "$"; //$NON-NLS-1$

	public static final String NO_TYPE = "none"; //$NON-NLS-1$
	public static final String UNDEFINED_TYPE = "undefined"; //$NON-NLS-1$
	public static final String VOID_TYPE = "void"; //$NON-NLS-1$
	public static final String NULL_TYPE = "null"; //$NON-NLS-1$

	public static final String PROTOTYPE_PROPERTY = "prototype"; //$NON-NLS-1$
	public static final String WINDOW_PROPERTY = "window"; //$NON-NLS-1$

	public static final String ARRAY_LITERAL = "[]"; //$NON-NLS-1$
	public static final String DYNAMIC_CLASS_PREFIX = "-dynamic-type-"; //$NON-NLS-1$
	public static final String GENERIC_OPEN = "<"; //$NON-NLS-1$
	public static final String GENERIC_CLOSE = ">"; //$NON-NLS-1$
	public static final String GENERIC_ARRAY_OPEN = ARRAY_TYPE + GENERIC_OPEN;
	public static final String GENERIC_CLASS_OPEN = CLASS_TYPE + GENERIC_OPEN;
	public static final String GENERIC_FUNCTION_OPEN = FUNCTION_TYPE + GENERIC_OPEN;

	public static final String FUNCTION_SIGNATURE_DELIMITER = ":"; //$NON-NLS-1$
	public static final String PARAMETER_TYPE_DELIMITER = "|"; //$NON-NLS-1$
	public static final String PARAMETER_DELIMITER = ","; //$NON-NLS-1$
	public static final String RETURN_TYPE_DELIMITER = ","; //$NON-NLS-1$

	// @formatter:off
	public static final Pattern FUNCTION_PREFIX = Pattern.compile(
		MessageFormat.format(
			"^({0}$|{0}{1}|{0}{2})", //$NON-NLS-1$
			FUNCTION_TYPE,
			FUNCTION_SIGNATURE_DELIMITER,
			GENERIC_OPEN
		)
	);
	// @formatter:on

	public static final String DEFAULT_ASSIGNMENT_TYPE = NUMBER_TYPE;
	public static final String DEFAULT_PARAMETER_TYPE = OBJECT_TYPE;

	private JSTypeConstants()
	{
	}
}
