/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.json.text.rules;

import com.aptana.core.util.StringUtil;

public interface IJSONScopes
{
	public static final String UNDEFINED = StringUtil.EMPTY;
	public static final String KEYWORD_OPERATOR = "keyword.operator.json"; //$NON-NLS-1$
	public static final String TRUE = "constant.language.boolean.true.json"; //$NON-NLS-1$
	public static final String FALSE = "constant.language.boolean.false.json"; //$NON-NLS-1$
	public static final String NULL = "constant.language.null.json"; //$NON-NLS-1$
	public static final String STRING_DOUBLE = "string.quoted.double.json"; //$NON-NLS-1$
	public static final String STRING_SINGLE = "string.quoted.single.json"; //$NON-NLS-1$
	public static final String COMMA = "meta.delimiter.object.comma.json"; //$NON-NLS-1$
	public static final String NUMBER = "constant.numeric.json"; //$NON-NLS-1$
	public static final String PROPERTY = "property.json"; //$NON-NLS-1$
	public static final String COMMENT = "comment.json"; //$NON-NLS-1$
	public static final String SOURCE = "source.json"; //$NON-NLS-1$
	public static final String CURLY = "meta.brace.curly.json"; //$NON-NLS-1$
	public static final String BRACKET = "meta.brace.square.json"; //$NON-NLS-1$

}
