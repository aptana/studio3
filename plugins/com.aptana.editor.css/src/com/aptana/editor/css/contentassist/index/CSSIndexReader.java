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
package com.aptana.editor.css.contentassist.index;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aptana.editor.css.contentassist.model.ElementElement;
import com.aptana.editor.css.contentassist.model.PropertyElement;
import com.aptana.editor.css.contentassist.model.UserAgentElement;
import com.aptana.editor.css.contentassist.model.ValueElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.QueryResult;
import com.aptana.index.core.SearchPattern;

public class CSSIndexReader
{
	/**
	 * CSSIndexReader
	 */
	public CSSIndexReader()
	{
	}

	/**
	 * createPropertyFromKey
	 * 
	 * @param index
	 * @param key
	 * @return
	 * @throws IOException
	 */
	private PropertyElement createPropertyFromKey(Index index, String key) throws IOException
	{
		String columns[] = key.split(CSSIndexConstants.DELIMITER);
		int column = 0;
		PropertyElement property = new PropertyElement();

		property.setName(columns[column++]);
		property.setAllowMultipleValues(Boolean.valueOf(columns[column++]));
		property.setType(columns[column++]);
		// TODO: specifications

		for (String userAgentKey : columns[column++].split(CSSIndexConstants.SUB_DELIMITER))
		{
			// get user agent and add to element
			property.addUserAgent(this.getUserAgent(index, userAgentKey));
		}

		property.setDescription(columns[column++]);
		property.setExample(columns[column++]);
		property.setHint(columns[column++]);
		property.setRemark(columns[column++]);

		if (column < columns.length)
		{
			for (String valueKey : columns[column++].split(CSSIndexConstants.SUB_DELIMITER))
			{
				property.addValue(this.getValue(index, valueKey));
			}
		}

		return property;
	}

	/**
	 * getElements
	 * 
	 * @param index
	 * @return
	 * @throws IOException
	 */
	public List<ElementElement> getElements(Index index) throws IOException
	{
		List<QueryResult> items = index.query(new String[] { CSSIndexConstants.ELEMENT }, "*", //$NON-NLS-1$
				SearchPattern.PATTERN_MATCH);
		List<ElementElement> result = new LinkedList<ElementElement>();

		if (items != null)
		{
			for (QueryResult queryResult : items)
			{
				String key = queryResult.getWord();
				String[] columns = key.split(CSSIndexConstants.DELIMITER);
				ElementElement element = new ElementElement();
				int column = 0;

				element.setName(columns[column++]);
				element.setDisplayName(columns[column++]);

				for (String userAgentKey : columns[column++].split(CSSIndexConstants.SUB_DELIMITER))
				{
					// get user agent and add to element
					element.addUserAgent(this.getUserAgent(index, userAgentKey));
				}

				element.setDescription(columns[column++]);
				element.setExample(columns[column++]);

				for (String property : columns[column++].split(CSSIndexConstants.SUB_DELIMITER))
				{
					element.addProperty(property);
				}

				element.setRemark(columns[column++]);

				result.add(element);
			}
		}

		return result;
	}

	/**
	 * getProperties
	 * 
	 * @param index
	 * @return
	 * @throws IOException
	 */
	public List<PropertyElement> getProperties(Index index) throws IOException
	{
		List<QueryResult> items = index.query(new String[] { CSSIndexConstants.PROPERTY }, "*", //$NON-NLS-1$
				SearchPattern.PATTERN_MATCH);
		List<PropertyElement> result = new LinkedList<PropertyElement>();

		if (items != null)
		{
			for (QueryResult queryResult : items)
			{
				String key = queryResult.getWord();
				PropertyElement property = this.createPropertyFromKey(index, key);

				result.add(property);
			}
		}

		return result;
	}

	/**
	 * getProperties
	 * 
	 * @param index
	 * @param names
	 * @return
	 * @throws IOException
	 */
	public List<PropertyElement> getProperties(Index index, String... names) throws IOException
	{
		List<PropertyElement> result = new LinkedList<PropertyElement>();

		for (String name : names)
		{
			String searchKey = name + CSSIndexConstants.DELIMITER;
			List<QueryResult> items = index.query(new String[] { CSSIndexConstants.PROPERTY }, searchKey,
					SearchPattern.PREFIX_MATCH);

			if (items != null)
			{
				for (QueryResult item : items)
				{
					String key = item.getWord();
					PropertyElement property = this.createPropertyFromKey(index, key);

					result.add(property);
				}
			}
		}

		return result;
	}

	/**
	 * getUserAgent
	 * 
	 * @param userAgentKey
	 * @return
	 * @throws IOException
	 */
	protected UserAgentElement getUserAgent(Index index, String userAgentKey) throws IOException
	{
		UserAgentElement result = CSSIndexWriter.userAgentsByKey.get(userAgentKey);
		
		if (result == null)
		{
			String searchKey = userAgentKey + CSSIndexConstants.DELIMITER;
			List<QueryResult> items = index.query(new String[] { CSSIndexConstants.USER_AGENT }, searchKey,
					SearchPattern.PREFIX_MATCH);
	
			if (items != null && items.size() > 0)
			{
				String key = items.get(0).getWord();
				String[] columns = key.split(CSSIndexConstants.DELIMITER);
				int column = 1; // skip index
	
				result = new UserAgentElement();
				result.setDescription(columns[column++]);
				result.setOS(columns[column++]);
				result.setPlatform(columns[column++]);
	
				// NOTE: split does not return a final empty element if the string being split
				// ends with the delimiter.
				if (column < columns.length)
				{
					result.setVersion(columns[column++]);
				}
			}
		}

		return result;
	}

	/**
	 * getValue
	 * 
	 * @param index
	 * @param valueKey
	 * @return
	 * @throws IOException
	 */
	private ValueElement getValue(Index index, String valueKey) throws IOException
	{
		String searchKey = valueKey + CSSIndexConstants.DELIMITER;
		List<QueryResult> items = index.query(new String[] { CSSIndexConstants.VALUE }, searchKey, SearchPattern.PREFIX_MATCH);
		ValueElement result = null;

		if (items != null && items.size() > 0)
		{
			String key = items.get(0).getWord();
			String[] columns = key.split(CSSIndexConstants.DELIMITER);
			int column = 1; // skip index

			result = new ValueElement();
			result.setName(columns[column++]);

			result.setDescription(columns[column++]);

			// NOTE: split does not return an empty element if the string being
			// split ends with the delimiter pattern. So, we have to make sure
			// we actually have a column for user agents before using it.
			if (column < columns.length)
			{
				for (String userAgentKey : columns[column++].split(CSSIndexConstants.SUB_DELIMITER))
				{
					// get user agent and add to element
					result.addUserAgent(this.getUserAgent(index, userAgentKey));
				}
			}
		}

		return result;
	}
	
	/**
	 * getValues
	 * 
	 * @return
	 */
	public Map<String, String> getValues(Index index, String category)
	{
		Map<String, String> result = null;

		if (index != null)
		{
			String pattern = "*"; //$NON-NLS-1$

			try
			{
				List<QueryResult> items = index.query(new String[] { category }, pattern, SearchPattern.PATTERN_MATCH);

				if (items != null && items.size() > 0)
				{
					result = new HashMap<String, String>();

					for (QueryResult item : items)
					{
						Set<String> paths = item.getDocuments();
						String path = (paths != null && !paths.isEmpty()) ? paths.iterator().next() : ""; //$NON-NLS-1$

						result.put(item.getWord(), path);
					}
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		return result;
	}
}
