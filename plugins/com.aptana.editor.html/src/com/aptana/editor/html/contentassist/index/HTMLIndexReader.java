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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.css.contentassist.index.CSSIndexConstants;
import com.aptana.editor.html.contentassist.model.AttributeElement;
import com.aptana.editor.html.contentassist.model.ElementElement;
import com.aptana.editor.html.contentassist.model.EntityElement;
import com.aptana.editor.html.contentassist.model.EventElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexReader;
import com.aptana.index.core.QueryResult;
import com.aptana.index.core.SearchPattern;

public class HTMLIndexReader extends IndexReader
{
	/**
	 * createAttribute
	 * 
	 * @param attribute
	 * @param key
	 * @return
	 */
	private AttributeElement createAttribute(QueryResult attribute)
	{
		return this.populateElement(new AttributeElement(), attribute, 1);
	}

	/**
	 * createElement
	 * 
	 * @param element
	 * @return
	 */
	private ElementElement createElement(QueryResult element)
	{
		return this.populateElement(new ElementElement(), element, 1);
	}

	/**
	 * createEntity
	 * 
	 * @param entity
	 * @return
	 */
	private EntityElement createEntity(QueryResult entity)
	{
		return this.populateElement(new EntityElement(), entity, 1);
	}

	/**
	 * createEvent
	 * 
	 * @param event
	 * @return
	 */
	private EventElement createEvent(QueryResult event)
	{
		return this.populateElement(new EventElement(), event, 1);
	}

	/**
	 * getAttributes
	 * 
	 * @return
	 * @throws IOException
	 */
	public List<AttributeElement> getAttributes(Index index) throws IOException
	{
		List<AttributeElement> result = new ArrayList<AttributeElement>();

		if (index != null)
		{
			List<QueryResult> attributes = index.query( //
				new String[] { HTMLIndexConstants.ATTRIBUTE }, //
				"*", //$NON-NLS-1$
				SearchPattern.PATTERN_MATCH //
				);

			if (attributes != null)
			{
				for (QueryResult attribute : attributes)
				{
					result.add(this.createAttribute(attribute));
				}
			}
		}

		return result;
	}

	/**
	 * getAttribute
	 * 
	 * @param index
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public List<AttributeElement> getAttributes(Index index, String... names) throws IOException
	{
		List<AttributeElement> result = new ArrayList<AttributeElement>();

		if (index != null && names != null)
		{
			for (String name : names)
			{
				List<QueryResult> attributes = index.query( //
					new String[] { HTMLIndexConstants.ATTRIBUTE }, //
					name + CSSIndexConstants.DELIMITER, //
					SearchPattern.PREFIX_MATCH //
					);

				if (attributes != null)
				{
					for (QueryResult attribute : attributes)
					{
						result.add(this.createAttribute(attribute));
					}
				}
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.IndexReader#getDelimiter()
	 */
	@Override
	protected String getDelimiter()
	{
		return HTMLIndexConstants.DELIMITER;
	}

	/**
	 * getElements
	 * 
	 * @return
	 * @throws IOException
	 */
	public List<ElementElement> getElements(Index index) throws IOException
	{
		List<ElementElement> result = new ArrayList<ElementElement>();

		if (index != null)
		{
			List<QueryResult> elements = index.query( //
				new String[] { HTMLIndexConstants.ELEMENT }, //
				"*", //$NON-NLS-1$
				SearchPattern.PATTERN_MATCH //
				);

			if (elements != null)
			{
				for (QueryResult element : elements)
				{
					result.add(this.createElement(element));
				}
			}
		}

		return result;
	}

	/**
	 * getElement
	 * 
	 * @param index
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public List<ElementElement> getElements(Index index, String... names) throws IOException
	{
		List<ElementElement> result = new ArrayList<ElementElement>();

		if (index != null && names != null)
		{
			for (String name : names)
			{
				List<QueryResult> elements = index.query( //
					new String[] { HTMLIndexConstants.ELEMENT }, //
					name + CSSIndexConstants.DELIMITER, //
					SearchPattern.PREFIX_MATCH //
					);

				if (elements != null)
				{
					for (QueryResult element : elements)
					{
						result.add(this.createElement(element));
					}
				}
			}
		}

		return result;
	}

	/**
	 * getEntities
	 * 
	 * @return
	 * @throws IOException
	 */
	public List<EntityElement> getEntities(Index index) throws IOException
	{
		List<EntityElement> result = new ArrayList<EntityElement>();

		if (index != null)
		{
			List<QueryResult> entities = index.query( //
				new String[] { HTMLIndexConstants.ENTITY }, //
				"*", //$NON-NLS-1$
				SearchPattern.PATTERN_MATCH //
				);

			if (entities != null)
			{
				for (QueryResult entity : entities)
				{
					result.add(this.createEntity(entity));
				}
			}
		}

		return result;
	}

	/**
	 * getEntity
	 * 
	 * @param index
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public EntityElement getEntity(Index index, String name) throws IOException
	{
		EntityElement result = null;

		if (index != null)
		{
			List<QueryResult> entities = index.query( //
				new String[] { HTMLIndexConstants.ENTITY }, //
				name + CSSIndexConstants.DELIMITER, //
				SearchPattern.PREFIX_MATCH //
				);

			if (entities != null)
			{
				for (QueryResult entity : entities)
				{
					result = this.createEntity(entity);

					// there should only be one match
					break;
				}
			}
		}

		return result;
	}

	/**
	 * getEvent
	 * 
	 * @param index
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public EventElement getEvent(Index index, String name) throws IOException
	{
		EventElement result = null;

		if (index != null)
		{
			List<QueryResult> events = index.query( //
				new String[] { HTMLIndexConstants.EVENT }, //
				name + CSSIndexConstants.DELIMITER, //
				SearchPattern.PREFIX_MATCH //
				);

			if (events != null)
			{
				for (QueryResult event : events)
				{
					result = this.createEvent(event);

					// there should only be one match
					break;
				}
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.IndexReader#getSubDelimiter()
	 */
	@Override
	protected String getSubDelimiter()
	{
		return HTMLIndexConstants.SUB_DELIMITER;
	}

	/**
	 * getValues
	 * 
	 * @return
	 */
	public Map<String, String> getValues(Index index, String category)
	{
		Map<String, String> result = null;

		if (index != null && StringUtil.isEmpty(category) == false)
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
