/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.parsing;

public interface IHTMLTagInfo
{
	/**
	 * tag info not defined
	 */
	public static final int UNKNOWN = 0;

	/**
	 * The close tag is required
	 */
	public static final int END_REQUIRED = 1;

	/**
	 * The close tag is optional
	 */
	public static final int END_OPTIONAL = 2;

	/**
	 * The close tag is forbidden
	 */
	public static final int END_FORBIDDEN = 4;

	/**
	 * Mask used to isolate the end tag info
	 */
	public static final int END_MASK = 7;

	/**
	 * Content of tag must be empty
	 */
	public static final int EMPTY = 8;
}
