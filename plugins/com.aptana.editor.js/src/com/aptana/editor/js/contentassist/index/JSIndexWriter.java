/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist.index;

import java.net.URI;
import java.util.List;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.js.JSTypeConstants;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexWriter;

public class JSIndexWriter extends IndexWriter
{
	/**
	 * getDocumentPath
	 * 
	 * @return
	 */
	protected URI getDocumentPath()
	{
		return URI.create(IJSIndexConstants.METADATA_FILE_LOCATION);
	}

	/**
	 * writeFunction
	 * 
	 * @param index
	 * @param function
	 * @param location
	 */
	protected void writeFunction(Index index, FunctionElement function, URI location)
	{
		// @formatter:off
		String value = StringUtil.join(
			IJSIndexConstants.DELIMITER,
			function.getOwningType(),
			function.getName(),
			this.serialize(function)
		);
		// @formatter:on

		index.addEntry(IJSIndexConstants.FUNCTION, value, location);
	}

	/**
	 * writeProperty
	 * 
	 * @param index
	 * @param property
	 * @param location
	 */
	protected void writeProperty(Index index, PropertyElement property, URI location)
	{
		// @formatter:off
		String value = StringUtil.join(
			IJSIndexConstants.DELIMITER,
			property.getOwningType(),
			property.getName(),
			this.serialize(property)
		);
		// @formatter:on

		index.addEntry(IJSIndexConstants.PROPERTY, value, location);
	}

	/**
	 * writeType
	 * 
	 * @param index
	 * @param type
	 */
	public void writeType(Index index, TypeElement type)
	{
		this.writeType(index, type, this.getDocumentPath());
	}

	/**
	 * writeType
	 * 
	 * @param index
	 * @param type
	 * @param location
	 */
	public void writeType(Index index, TypeElement type, URI location)
	{
		if (index != null && type != null && location != null)
		{
			List<String> parentTypes = type.getParentTypes();
			String parentType;

			if (!parentTypes.isEmpty())
			{
				parentType = StringUtil.join(IJSIndexConstants.SUB_DELIMITER, parentTypes);
			}
			else if (!type.getName().equals(JSTypeConstants.OBJECT_TYPE))
			{
				parentType = JSTypeConstants.OBJECT_TYPE;
			}
			else
			{
				parentType = StringUtil.EMPTY;
			}
			// SinceElement[] sinceList = type.getSinceList();
			// UserAgentElement[] userAgents = type.getUserAgents();

			// calculate key value and add to index
			// @formatter:off
			String value = StringUtil.join(
				IJSIndexConstants.DELIMITER,
				type.getName(),
				parentType,
				type.getDescription()
			);
			// @formatter:on

			index.addEntry(IJSIndexConstants.TYPE, value, location);

			// write properties
			for (PropertyElement property : type.getProperties())
			{
				if (property instanceof FunctionElement)
				{
					this.writeFunction(index, (FunctionElement) property, location);
				}
				else
				{
					this.writeProperty(index, property, location);
				}
			}
		}
	}
}
