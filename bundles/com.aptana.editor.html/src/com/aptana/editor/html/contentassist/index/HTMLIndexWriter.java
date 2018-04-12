/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.contentassist.index;

import java.net.URI;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.html.contentassist.model.AttributeElement;
import com.aptana.editor.html.contentassist.model.ElementElement;
import com.aptana.editor.html.contentassist.model.EntityElement;
import com.aptana.editor.html.contentassist.model.EventElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexWriter;

public class HTMLIndexWriter extends IndexWriter
{
	/**
	 * getDocumentPath
	 * 
	 * @return
	 */
	protected URI getDocumentPath()
	{
		return URI.create(IHTMLIndexConstants.METADATA_INDEX_LOCATION);
	}

	/**
	 * writeAttribute
	 * 
	 * @param index
	 * @param attribute
	 */
	public void writeAttribute(Index index, AttributeElement attribute)
	{
		if (index != null && attribute != null)
		{
			String key = StringUtil.join( //
				IHTMLIndexConstants.DELIMITER, //
				attribute.getName(), //
				this.serialize(attribute) //
				);

			index.addEntry(IHTMLIndexConstants.ATTRIBUTE, key, this.getDocumentPath());
		}
	}

	/**
	 * writeElement
	 * 
	 * @param index
	 * @param element
	 */
	public void writeElement(Index index, ElementElement element)
	{
		if (index != null && element != null)
		{
			String key = StringUtil.join( //
				IHTMLIndexConstants.DELIMITER, //
				element.getName(), //
				this.serialize(element) //
				);

			index.addEntry(IHTMLIndexConstants.ELEMENT, key, this.getDocumentPath());
		}
	}

	/**
	 * writeEntity
	 * 
	 * @param index
	 * @param entity
	 */
	public void writeEntity(Index index, EntityElement entity)
	{
		if (index != null && entity != null)
		{
			String key = StringUtil.join( //
				IHTMLIndexConstants.DELIMITER, //
				entity.getName(), //
				this.serialize(entity) //
				);

			index.addEntry(IHTMLIndexConstants.ENTITY, key, this.getDocumentPath());
		}
	}

	/**
	 * writeEvent
	 * 
	 * @param index
	 * @param event
	 */
	public void writeEvent(Index index, EventElement event)
	{
		if (index != null && event != null)
		{
			String key = StringUtil.join( //
				IHTMLIndexConstants.DELIMITER, //
				event.getName(), //
				this.serialize(event) //
				);

			index.addEntry(IHTMLIndexConstants.EVENT, key, this.getDocumentPath());
		}
	}
}
