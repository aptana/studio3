/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.parsing.sdoc.model;

public class NamespaceTag extends TagWithName
{
	/**
	 * NamespaceTag
	 * 
	 * @param name
	 * @param text
	 */
	public NamespaceTag(String name, String text)
	{
		super(TagType.NAMESPACE, name, text);
	}
}
