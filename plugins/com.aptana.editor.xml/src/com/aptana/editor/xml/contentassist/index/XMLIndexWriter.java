/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.contentassist.index;

import java.net.URI;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.xml.contentassist.model.AttributeElement;
import com.aptana.editor.xml.contentassist.model.ElementElement;
import com.aptana.index.core.Index;

public class XMLIndexWriter
{
	private IKeyProvider _keyProvider;

	/**
	 * HTMLIndexWriter
	 */
	public XMLIndexWriter(IKeyProvider keyProvider)
	{
		this._keyProvider = keyProvider;
	}

	/**
	 * getDocumentPath
	 * 
	 * @return
	 */
	protected URI getDocumentPath()
	{
		return URI.create(this._keyProvider.getMetadataLocation());
	}

	/**
	 * writeAttribute
	 * 
	 * @param index
	 * @param attribute
	 */
	public void writeAttribute(Index index, AttributeElement attribute)
	{
		this.writeAttribute(index, attribute, this.getDocumentPath());
	}

	/**
	 * writeAttribute
	 * 
	 * @param index
	 * @param attribute
	 */
	public void writeAttribute(Index index, AttributeElement attribute, URI location)
	{
		String[] columns = new String[] { //
		attribute.getName(), //
			attribute.getElement(), //
			attribute.getDescription(), //
			"" // values //$NON-NLS-1$
		};
		String key = StringUtil.join(IXMLIndexConstants.DELIMITER, columns);

		index.addEntry(this._keyProvider.getAttributeKey(), key, location);
	}

	/**
	 * writeElement
	 * 
	 * @param index
	 * @param element
	 */
	public void writeElement(Index index, ElementElement element)
	{
		this.writeElement(index, element, this.getDocumentPath());
	}

	/**
	 * writeElement
	 * 
	 * @param index
	 * @param element
	 */
	public void writeElement(Index index, ElementElement element, URI location)
	{
		String[] columns = new String[] { //
		element.getName(), //
			element.getDisplayName(), //
			StringUtil.join(IXMLIndexConstants.SUB_DELIMITER, element.getAttributes()), //
			element.getDescription() //
		};
		String key = StringUtil.join(IXMLIndexConstants.DELIMITER, columns);

		index.addEntry(this._keyProvider.getElementKey(), key, location);
	}
}
