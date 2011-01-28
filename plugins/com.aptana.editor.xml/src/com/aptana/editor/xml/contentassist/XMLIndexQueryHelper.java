/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.contentassist;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import com.aptana.editor.xml.contentassist.index.IKeyProvider;
import com.aptana.editor.xml.contentassist.index.XMLIndexReader;
import com.aptana.editor.xml.contentassist.index.XMLKeyProvider;
import com.aptana.editor.xml.contentassist.model.AttributeElement;
import com.aptana.editor.xml.contentassist.model.ElementElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;

public class XMLIndexQueryHelper
{
	private IKeyProvider _keyProvider;
	private URI _metadataLocation;
	private XMLIndexReader _reader;

	/**
	 * XMLIndexQueryHelper
	 */
	public XMLIndexQueryHelper()
	{
		this._keyProvider = this.createKeyProvider();
		this._metadataLocation = URI.create(this._keyProvider.getMetadataLocation());
		this._reader = new XMLIndexReader(this._keyProvider);
	}

	/**
	 * createKeyProvider
	 * 
	 * @return
	 */
	protected IKeyProvider createKeyProvider()
	{
		return new XMLKeyProvider();
	}

	/**
	 * getAttribute
	 * 
	 * @param elementName
	 * @param attributeName
	 * @return
	 */
	public AttributeElement getAttribute(String elementName, String attributeName)
	{
		return null;
	}

	/**
	 * getElement
	 * 
	 * @param elementName
	 * @return
	 */
	public ElementElement getElement(String elementName)
	{
		ElementElement result = null;

		try
		{
			result = this._reader.getElement(this.getIndex(), elementName);
		}
		catch (IOException e)
		{
		}

		return result;
	}

	/**
	 * getElements
	 * 
	 * @return
	 */
	public List<ElementElement> getElements()
	{
		List<ElementElement> result = Collections.emptyList();

		try
		{
			result = this._reader.getElements(this.getIndex());
		}
		catch (IOException e)
		{
		}

		return result;
	}

	/**
	 * getIndex
	 * 
	 * @return
	 */
	public Index getIndex()
	{
		return IndexManager.getInstance().getIndex(this._metadataLocation);
	}
}
