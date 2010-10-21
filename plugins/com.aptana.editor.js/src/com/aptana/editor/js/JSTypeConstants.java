/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js;

import java.text.MessageFormat;
import java.util.regex.Pattern;

public class JSTypeConstants
{
	public static final String ARRAY_TYPE = "Array"; //$NON-NLS-1$
	public static final String BOOLEAN_TYPE = "Boolean"; //$NON-NLS-1$
	public static final String CLASS_TYPE = "Class"; //$NON-NLS-1$
	public static final String FUNCTION_TYPE = "Function"; //$NON-NLS-1$
	public static final String NUMBER_TYPE = "Number"; //$NON-NLS-1$
	public static final String OBJECT_TYPE = "Object"; //$NON-NLS-1$
	public static final String REG_EXP_TYPE = "RegExp"; //$NON-NLS-1$
	public static final String STRING_TYPE = "String"; //$NON-NLS-1$
	public static final String WINDOW_TYPE = "Window"; //$NON-NLS-1$
	public static final String USER_TYPE = "UserType"; //$NON-NLS-1$

	public static final String UNDEFINED_TYPE = "undefined"; //$NON-NLS-1$
	public static final String VOID_TYPE = "void"; //$NON-NLS-1$
	public static final String NULL_TYPE = "null"; //$NON-NLS-1$

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
	public static final String RETURN_TYPE_DELIMITER = ","; //$NON-NLS-1$

	public static final Pattern FUNCTION_PREFIX = Pattern.compile( //
		MessageFormat.format( //
			"^({0}$|{0}{1}|{0}{2})", // //$NON-NLS-1$
			FUNCTION_TYPE, //
			FUNCTION_SIGNATURE_DELIMITER, //
			GENERIC_OPEN //
			) //
		); //

	public static final String DEFAULT_ASSIGNMENT_TYPE = NUMBER_TYPE;
	public static final String DEFAULT_PARAMETER_TYPE = OBJECT_TYPE;

	private JSTypeConstants()
	{
	}
}
