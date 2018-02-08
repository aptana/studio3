/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.xml.core.index;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.index.core.Index;
import com.aptana.index.core.QueryResult;
import com.aptana.index.core.SearchPattern;
import com.aptana.xml.core.model.AttributeElement;
import com.aptana.xml.core.model.ElementElement;
import com.aptana.xml.core.model.ValueElement;

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
		List<QueryResult> items = index.query(new String[] { this._keyProvider.getElementKey() }, searchKey,
				SearchPattern.PREFIX_MATCH);
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
		List<QueryResult> items = index.query(new String[] { this._keyProvider.getElementKey() },
				"*", SearchPattern.PATTERN_MATCH); //$NON-NLS-1$
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

	public AttributeElement getAttribute(Index index, String elementName, String attributeName)
	{
		String searchKey = StringUtil.join(IXMLIndexConstants.DELIMITER, attributeName, elementName, "*"); //$NON-NLS-1$
		List<QueryResult> items = index.query(new String[] { this._keyProvider.getAttributeKey() }, searchKey,
				SearchPattern.PATTERN_MATCH);

		if (!CollectionsUtil.isEmpty(items))
		{
			for (QueryResult item : items)
			{
				String key = item.getWord();
				return createAttributeFromKey(key);
			}
		}

		return null;
	}

	private AttributeElement createAttributeFromKey(String key)
	{
		String[] columns = key.split(IXMLIndexConstants.DELIMITER);
		AttributeElement attribute = new AttributeElement();
		int column = 0;

		attribute.setName(columns[column++]);
		attribute.setElement(columns[column++]);
		attribute.setDescription(columns[column++]);

		if (column < columns.length)
		{
			for (String valueName : columns[column++].split(IXMLIndexConstants.SUB_DELIMITER))
			{
				ValueElement value = new ValueElement();
				value.setName(valueName);
				attribute.addValue(value);
			}
		}
		return attribute;
	}
}
