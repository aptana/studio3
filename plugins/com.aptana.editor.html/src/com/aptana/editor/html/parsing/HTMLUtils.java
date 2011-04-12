/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.parsing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aptana.editor.html.contentassist.HTMLIndexQueryHelper;
import com.aptana.editor.html.contentassist.model.EventElement;

public class HTMLUtils
{
	/**
	 * Determine if the specified attribute name indicates an attribute that may contain CSS
	 * 
	 * @param name
	 * @return
	 */
	public static boolean isCSSAttribute(String name)
	{
		boolean result = false;

		if (name != null)
		{
			result = name.equalsIgnoreCase("style"); //$NON-NLS-1$
		}

		return result;
	}

	/**
	 * Determine if the specified attribute name indicates an attribute that may contain JS
	 * 
	 * @param elementName
	 * @param attributeName
	 * @return
	 */
	public static boolean isJSAttribute(String elementName, String attributeName)
	{
		boolean result = false;

		if (elementName != null && attributeName != null)
		{
			attributeName = attributeName.toLowerCase();

			for (EventElement event : new HTMLIndexQueryHelper().getEvents(elementName))
			{
				if (event.getName().equals(attributeName))
				{
					result = true;
					break;
				}
			}
		}

		return result;
	}

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
	
	/**
	 * Returns true if the specified tag contents has self-closing tag
	 * @param tagContents
	 * @return
	 */
	public static boolean isTagSelfClosing(String tagContents)
	{
		return tagContents.endsWith("/>"); //$NON-NLS-1$
	}
	
	/**
	 * Returns true if the specified tag contents has JavaScript &lt;script&gt; tag
	 * @param tagContents
	 * @return
	 */
	public static boolean isJavaScriptTag(String tagContents)
	{
		String type = getTagAttribute(tagContents, "type"); //$NON-NLS-1$
		if (type != null && type.toLowerCase().contains("javascript")) //$NON-NLS-1$
		{
			return true;
		}
		String language = getTagAttribute(tagContents, "language"); //$NON-NLS-1$
		if (language != null && language.toLowerCase().contains("javascript")) //$NON-NLS-1$
		{
			return true;
		}
		return type == null && language == null;
	}
	
	private static String getTagAttribute(String tagContents, String attributeName)
	{
		Matcher matcher = Pattern.compile(".*\\s+"+attributeName+"=\"([a-zA-Z_/0-9-]+)\".*").matcher(tagContents.toLowerCase()); //$NON-NLS-1$ //$NON-NLS-2$
		if (matcher.matches())
		{
			return matcher.group(1);
		}
		return null;
	}
}
