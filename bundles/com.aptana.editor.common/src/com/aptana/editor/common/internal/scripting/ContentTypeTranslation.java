/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common.internal.scripting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aptana.editor.common.scripting.IContentTypeTranslator;
import com.aptana.editor.common.scripting.QualifiedContentType;

/**
 * @author Max Stepanov
 */
public class ContentTypeTranslation implements IContentTypeTranslator
{

	private static ContentTypeTranslation instance;

	private Map<QualifiedContentType, QualifiedContentType> map = new HashMap<QualifiedContentType, QualifiedContentType>();

	private ContentTypeTranslation()
	{
	}

	public static ContentTypeTranslation getDefault()
	{
		if (instance == null)
		{
			instance = new ContentTypeTranslation();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.scripting.IContentTypeTranslator#addTranslation(com.aptana.editor.common.QualifiedContentType, com.aptana.editor.common.QualifiedContentType)
	 */
	public void addTranslation(QualifiedContentType left, QualifiedContentType right)
	{
		map.put(left, right);
	}

	public QualifiedContentType translate(QualifiedContentType contentType)
	{
		QualifiedContentType i = contentType;
		QualifiedContentType result;
		List<String> parts = new ArrayList<String>();
		// Chop off last portion of scope until we find that the full scope is in our translation map
		while ((result = map.get(i)) == null && i.getPartCount() > 0)
		{
			parts.add(0, i.getLastPart());
			i = i.supertype();
		}
		if (result != null)
		{
			// Remaining scope is in our translation mapping
			for (String part : parts)
			{
				// Now try to translate the chopped off parts
				QualifiedContentType qtype = new QualifiedContentType(part);
				if (map.containsKey(qtype))
				{
					result = result.subtype(map.get(qtype).getParts());
				}
			}
			return result;
		}
		return contentType;
	}

}
