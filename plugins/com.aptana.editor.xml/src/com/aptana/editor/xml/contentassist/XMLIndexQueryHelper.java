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
