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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aptana.editor.css.contentassist.index.CSSIndexConstants;
import com.aptana.editor.html.contentassist.model.ElementElement;
import com.aptana.editor.html.contentassist.model.UserAgentElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.QueryResult;
import com.aptana.index.core.SearchPattern;

public class HTMLIndexReader
{
	/**
	 * createElement
	 * 
	 * @param key
	 * @return
	 * @throws IOException
	 */
	private ElementElement createElementFromKey(Index index, String key) throws IOException
	{
		String[] columns = key.split(HTMLIndexConstants.DELIMITER);
		ElementElement element = new ElementElement();
		int column = 0;

		element.setName(columns[column++]);
		element.setDisplayName(columns[column++]);
		element.setRelatedClass(columns[column++]);

		for (String attribute : columns[column++].split(HTMLIndexConstants.SUB_DELIMITER))
		{
			element.addAttribute(attribute);
		}

		for (String userAgentKey : columns[column++].split(HTMLIndexConstants.SUB_DELIMITER))
		{
			element.addUserAgent(this.getUserAgent(index, userAgentKey));
		}

		element.setDeprecated(columns[column++]);
		element.setDescription(columns[column++]);

		for (String event : columns[column++].split(HTMLIndexConstants.SUB_DELIMITER))
		{
			element.addEvent(event);
		}

		element.setExample(columns[column++]);

		for (String reference : columns[column++].split(HTMLIndexConstants.SUB_DELIMITER))
		{
			element.addReference(reference);
		}

		element.setRemark(columns[column++]);

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
		String searchKey = name + CSSIndexConstants.DELIMITER;
		List<QueryResult> items = index.query(new String[] { HTMLIndexConstants.ELEMENT }, searchKey,
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
	public List<ElementElement> getElements(Index index) throws IOException
	{
		List<QueryResult> items = index.query(new String[] { HTMLIndexConstants.ELEMENT },
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

	/**
	 * getUserAgent
	 * 
	 * @param userAgentKey
	 * @return
	 * @throws IOException
	 */
	protected UserAgentElement getUserAgent(Index index, String userAgentKey) throws IOException
	{
		String searchKey = userAgentKey + HTMLIndexConstants.DELIMITER;
		List<QueryResult> items = index.query(new String[] { HTMLIndexConstants.USER_AGENT }, searchKey,
				SearchPattern.PREFIX_MATCH);
		UserAgentElement result = null;

		if (items != null && items.size() > 0)
		{
			String key = items.get(0).getWord();
			String[] columns = key.split(HTMLIndexConstants.DELIMITER);
			int column = 1; // skip index

			result = new UserAgentElement();
			result.setPlatform(columns[column++]);

			// NOTE: split does not return a final empty element if the string being split
			// ends with the delimiter.
			if (column < columns.length)
			{
				result.setVersion(columns[column++]);
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
						try
						{
							URI uri = index.getRelativeDocumentPath(new URI(path));
							result.put(item.getWord(), uri.toString());
						}
						catch (URISyntaxException e)
						{
							result.put(item.getWord(), path);
						}
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
