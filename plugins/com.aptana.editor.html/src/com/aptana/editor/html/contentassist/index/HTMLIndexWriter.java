/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
		return URI.create(HTMLIndexConstants.METADATA_INDEX_LOCATION);
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
				HTMLIndexConstants.DELIMITER, //
				attribute.getName(), //
				this.serialize(attribute) //
				);

			index.addEntry(HTMLIndexConstants.ATTRIBUTE, key, this.getDocumentPath());
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
				HTMLIndexConstants.DELIMITER, //
				element.getName(), //
				this.serialize(element) //
				);

			index.addEntry(HTMLIndexConstants.ELEMENT, key, this.getDocumentPath());
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
				HTMLIndexConstants.DELIMITER, //
				entity.getName(), //
				this.serialize(entity) //
				);

			index.addEntry(HTMLIndexConstants.ENTITY, key, this.getDocumentPath());
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
				HTMLIndexConstants.DELIMITER, //
				event.getName(), //
				this.serialize(event) //
				);

			index.addEntry(HTMLIndexConstants.EVENT, key, this.getDocumentPath());
		}
	}
}
