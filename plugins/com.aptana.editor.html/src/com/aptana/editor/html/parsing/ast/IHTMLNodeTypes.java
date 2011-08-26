/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.parsing.ast;

public interface IHTMLNodeTypes
{
	public static final short ERROR = -1;

	public static final short UNKNOWN = 0;

	public static final short DECLARATION = 1;

	public static final short ELEMENT = 2;

	/**
	 * Used to indicate a transition to another language
	 */
	public static final short SPECIAL = 3;

	public static final short COMMENT = 4;

	public static final short TEXT = 5;
}
