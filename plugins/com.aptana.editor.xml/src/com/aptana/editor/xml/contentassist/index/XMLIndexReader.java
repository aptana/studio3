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
package com.aptana.editor.xml.contentassist.index;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.aptana.editor.xml.contentassist.model.ElementElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.QueryResult;
import com.aptana.index.core.SearchPattern;

public class XMLIndexReader
{
	private IKeyProvider _keyProvider;

	/**
	 * XMLIndexReader
	 * 
	 * @param keyProvider
	 */
	public XMLIndexReader(IKeyProvider keyProvider)
	{
		this._keyProvider = keyProvider;
	}

	/**
	 * createElement
	 * 
	 * @param key
	 * @return
	 * @throws IOException
	 */
	private ElementElement createElementFromKey(Index index, String key) throws IOException
	{
		String[] columns = key.split(XMLIndexConstants.DELIMITER);
		ElementElement element = new ElementElement();
		int column = 0;

		element.setName(columns[column++]);
		element.setDisplayName(columns[column++]);

		for (String attribute : columns[column++].split(XMLIndexConstants.SUB_DELIMITER))
		{
			element.addAttribute(attribute);
		}

		if (column < columns.length)
		{
			element.setDescription(columns[column++]);
		}

		return element;
	}

	/**
	 * getElement
	 * 
	 * @param index
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public ElementElement getElement(Index index, String name) throws IOException
	{
		String searchKey = name + XMLIndexConstants.DELIMITER;
		List<QueryResult> items = index.query(new String[] { this._keyProvider.getElementKey() }, searchKey, SearchPattern.PREFIX_MATCH);
		ElementElement result = null;

		if (items != null)
		{
			for (QueryResult item : items)
			{
				String key = item.getWord();

				result = this.createElementFromKey(index, key);

				break;
			}
		}

		return result;
	}

	/**
	 * getElements
	 * 
	 * @return
	 * @throws IOException
	 */
	public List<ElementElement> getElements(Index index) throws IOException
	{
		List<QueryResult> items = index.query(new String[] { this._keyProvider.getElementKey() }, "*", SearchPattern.PATTERN_MATCH); //$NON-NLS-1$
		List<ElementElement> result = new LinkedList<ElementElement>();

		if (items != null)
		{
			for (QueryResult item : items)
			{
				String key = item.getWord();
				ElementElement element = this.createElementFromKey(index, key);

				result.add(element);
			}
		}

		return result;
	}
}
