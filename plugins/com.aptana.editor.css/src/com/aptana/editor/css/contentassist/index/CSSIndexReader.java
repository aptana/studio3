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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mortbay.util.ajax.JSON;

import com.aptana.editor.css.contentassist.model.ElementElement;
import com.aptana.editor.css.contentassist.model.PropertyElement;
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
	 * createElementFromKey
	 * 
	 * @param index
	 * @param key
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	private ElementElement createElementFromKey(Index index, QueryResult element) throws IOException
	{
		ElementElement e = new ElementElement();

		String key = element.getWord();
		String[] columns = key.split(CSSIndexConstants.DELIMITER);

		Object m = JSON.parse(columns[1]);

		if (m instanceof Map)
		{
			e.fromJSON((Map) m);
		}

		for (String document : element.getDocuments())
		{
			e.addDocument(document);
		}

		return e;
	}

	/**
	 * createPropertyFromKey
	 * 
	 * @param index
	 * @param key
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	private PropertyElement createPropertyFromKey(Index index, QueryResult property) throws IOException
	{
		PropertyElement p = new PropertyElement();

		String key = property.getWord();
		String columns[] = key.split(CSSIndexConstants.DELIMITER);

		Object m = JSON.parse(columns[1]);

		if (m instanceof Map)
		{
			p.fromJSON((Map) m);
		}

		for (String document : property.getDocuments())
		{
			p.addDocument(document);
		}

		return p;
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
		List<ElementElement> result = new ArrayList<ElementElement>();

		if (index != null)
		{
			List<QueryResult> items = index.query( //
				new String[] { CSSIndexConstants.ELEMENT }, //
				"*", //$NON-NLS-1$
				SearchPattern.PATTERN_MATCH //
				);

			if (items != null)
			{
				for (QueryResult element : items)
				{
					result.add(this.createElementFromKey(index, element));
				}
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
		List<PropertyElement> result = new ArrayList<PropertyElement>();

		if (index != null)
		{
			List<QueryResult> properties = index.query( //
				new String[] { CSSIndexConstants.PROPERTY }, //
				"*", //$NON-NLS-1$
				SearchPattern.PATTERN_MATCH //
				);

			if (properties != null)
			{
				for (QueryResult property : properties)
				{
					result.add(this.createPropertyFromKey(index, property));
				}
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
		List<PropertyElement> result = new ArrayList<PropertyElement>();

		if (index != null)
		{
			for (String name : names)
			{
				List<QueryResult> properties = index.query( //
					new String[] { CSSIndexConstants.PROPERTY }, //
					name + CSSIndexConstants.DELIMITER, //
					SearchPattern.PREFIX_MATCH //
					);

				if (properties != null)
				{
					for (QueryResult property : properties)
					{
						result.add(this.createPropertyFromKey(index, property));
					}
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
