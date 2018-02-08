/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.contentassist;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * UserAgentFilterType
 */
public enum UserAgentFilterType
{
	NO_FILTER("none"), //$NON-NLS-1$
	ONE_OR_MORE("some"), //$NON-NLS-1$
	ALL("all"); //$NON-NLS-1$

	private static final Map<String, UserAgentFilterType> textMap;

	static
	{
		textMap = new HashMap<String, UserAgentFilterType>();

		for (UserAgentFilterType type : EnumSet.allOf(UserAgentFilterType.class))
		{
			textMap.put(type.getText(), type);
		}
	}

	/**
	 * Get the enumeration value with the associate text value
	 * 
	 * @param text
	 * @return
	 */
	public static UserAgentFilterType get(String text)
	{
		UserAgentFilterType result = NO_FILTER;

		if (textMap.containsKey(text))
		{
			result = textMap.get(text);
		}

		return result;
	}

	private String text;

	/**
	 * Construct a user agent filter enumeration value with an associated text value
	 * 
	 * @param text
	 */
	private UserAgentFilterType(String text)
	{
		this.text = text;
	}

	/**
	 * Return the text value of this enumeration value
	 * 
	 * @return
	 */
	public String getText()
	{
		return text;
	}
}
