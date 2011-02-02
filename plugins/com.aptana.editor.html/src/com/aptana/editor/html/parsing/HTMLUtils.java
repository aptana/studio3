/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.parsing;

public class HTMLUtils
{

	/**
	 * Removes the "<" and "</" from the beginning and ">" from the end of a tag.
	 * 
	 * @param tag
	 *            the tag text to strip
	 * @return a string with the necessary items removed
	 */
	public static String stripTagEndings(String tag)
	{
		String name = tag.replaceAll("^\\s*</", ""); //$NON-NLS-1$ //$NON-NLS-2$
		name = name.replaceAll(">\\s*$", ""); //$NON-NLS-1$ //$NON-NLS-2$
		return name.replaceAll("^\\s*<", ""); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
