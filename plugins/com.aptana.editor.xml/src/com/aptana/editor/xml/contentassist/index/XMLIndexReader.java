/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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
	private ElementElement createElementFromKey(Index index, String key)
	{
		String[] columns = key.split(IXMLIndexConstants.DELIMITER);
		ElementElement element = new ElementElement();
		int column = 0;

		element.setName(columns[column++]);
		element.setDisplayName(columns[column++]);

		for (String attribute : columns[column++].split(IXMLIndexConstants.SUB_DELIMITER))
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
	public ElementElement getElement(Index index, String name)
	{
		String searchKey = name + IXMLIndexConstants.DELIMITER;
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
	public List<ElementElement> getElements(Index index)
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
