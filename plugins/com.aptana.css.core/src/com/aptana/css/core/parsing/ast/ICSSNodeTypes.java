/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.parsing.ast;

public interface ICSSNodeTypes
{
	public static final short UNKNOWN = 0;
	public static final short ATTRIBUTE_SELECTOR = 1;
	public static final short FUNCTION = 2;
	public static final short CHAR_SET = 3;
	public static final short DECLARATION = 4;
	public static final short IMPORT = 5;
	public static final short RULE = 6;
	public static final short MEDIA = 7;
	public static final short PAGE = 8;
	public static final short SELECTOR = 9;
	public static final short SIMPLE_SELECTOR = 10;
	public static final short EXPRESSION = 11;
	public static final short TERM = 12;
	public static final short TERM_LIST = 13;
	public static final short COMMENT = 14;
	public static final short FONTFACE = 15;
	public static final short NAMESPACE = 16;
	public static final short PAGE_SELECTOR = 17;
	public static final short TEXT = 18;
	public static final short AT_RULE = 19;
	public static final short LIST = 20;
	public static final short MOZ_DOCUMENT = 21;
	public static final short MS_VIEWPORT = 22;
}
