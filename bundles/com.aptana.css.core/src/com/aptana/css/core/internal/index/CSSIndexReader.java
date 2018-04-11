/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.internal.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aptana.core.util.StringUtil;
import com.aptana.css.core.index.ICSSIndexConstants;
import com.aptana.css.core.model.ElementElement;
import com.aptana.css.core.model.PropertyElement;
import com.aptana.css.core.model.PseudoClassElement;
import com.aptana.css.core.model.PseudoElementElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexReader;
import com.aptana.index.core.QueryResult;
import com.aptana.index.core.SearchPattern;

public class CSSIndexReader extends IndexReader
{
	private static final String WILD_CARD_CHAR = "*"; //$NON-NLS-1$

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
	 * createProperty
	 * 
	 * @param property
	 * @return
	 */
	private PropertyElement createProperty(QueryResult property)
	{
		return this.populateElement(new PropertyElement(), property, 1);
	}

	/**
	 * createPseudoClass
	 * 
	 * @param pseudoClass
	 * @return
	 */
	private PseudoClassElement createPseudoClass(QueryResult pseudoClass)
	{
		return this.populateElement(new PseudoClassElement(), pseudoClass);
	}

	/**
	 * @param pseudoElement
	 * @return
	 */
	private PseudoElementElement createPseudoElement(QueryResult pseudoElement)
	{
		return this.populateElement(new PseudoElementElement(), pseudoElement);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.IndexReader#getDelimiter()
	 */
	@Override
	protected String getDelimiter()
	{
		return ICSSIndexConstants.DELIMITER;
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
					new String[] { ICSSIndexConstants.ELEMENT }, //
					WILD_CARD_CHAR, SearchPattern.PATTERN_MATCH //
					);

			if (items != null)
			{
				for (QueryResult element : items)
				{
					result.add(this.createElement(element));
				}
			}
		}

		return result;
	}

	public List<ElementElement> getElements(Index index, String... names) throws IOException
	{
		List<ElementElement> result = new ArrayList<ElementElement>();

		if (index != null && names != null)
		{
			for (String name : names)
			{
				List<QueryResult> elements = index.query( //
						new String[] { ICSSIndexConstants.ELEMENT }, //
						name + ICSSIndexConstants.DELIMITER, //
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
					new String[] { ICSSIndexConstants.PROPERTY }, //
					WILD_CARD_CHAR, SearchPattern.PATTERN_MATCH //
					);

			if (properties != null)
			{
				for (QueryResult property : properties)
				{
					result.add(this.createProperty(property));
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

		if (index != null && names != null)
		{
			for (String name : names)
			{
				List<QueryResult> properties = index.query( //
						new String[] { ICSSIndexConstants.PROPERTY }, //
						name + ICSSIndexConstants.DELIMITER, //
						SearchPattern.PREFIX_MATCH //
						);

				if (properties != null)
				{
					for (QueryResult property : properties)
					{
						result.add(this.createProperty(property));
					}
				}
			}
		}

		return result;
	}

	/**
	 * getPseudoClasses
	 * 
	 * @param index
	 * @return
	 * @throws IOException
	 */
	public List<PseudoClassElement> getPseudoClasses(Index index) throws IOException
	{
		List<PseudoClassElement> result = new ArrayList<PseudoClassElement>();

		if (index != null)
		{
			List<QueryResult> pseudoClasses = index.query( //
					new String[] { ICSSIndexConstants.PSUEDO_CLASS }, //
					WILD_CARD_CHAR, SearchPattern.PATTERN_MATCH //
					);

			if (pseudoClasses != null)
			{
				for (QueryResult pseudoClass : pseudoClasses)
				{
					result.add(this.createPseudoClass(pseudoClass));
				}
			}
		}

		return result;
	}

	/**
	 * getPseudoElements
	 * 
	 * @param index
	 * @return
	 * @throws IOException
	 */
	public List<PseudoElementElement> getPseudoElements(Index index) throws IOException
	{
		List<PseudoElementElement> result = new ArrayList<PseudoElementElement>();

		if (index != null)
		{
			List<QueryResult> pseudoElements = index.query( //
					new String[] { ICSSIndexConstants.PSUEDO_ELEMENT }, //
					WILD_CARD_CHAR, SearchPattern.PATTERN_MATCH //
					);

			if (pseudoElements != null)
			{
				for (QueryResult pseudoElement : pseudoElements)
				{
					result.add(this.createPseudoElement(pseudoElement));
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
		return ICSSIndexConstants.SUB_DELIMITER;
	}

	/**
	 * getValues
	 * 
	 * @return
	 */
	public Map<String, String> getValues(Index index, String category)
	{
		Map<String, String> result = null;

		if (index != null && !StringUtil.isEmpty(category))
		{
			String pattern = WILD_CARD_CHAR;
			List<QueryResult> items = index.query(new String[] { category }, pattern, SearchPattern.PATTERN_MATCH);
			if (items != null && items.size() > 0)
			{
				result = new HashMap<String, String>();

				for (QueryResult item : items)
				{
					Set<String> paths = item.getDocuments();
					String path = (paths != null && !paths.isEmpty()) ? paths.iterator().next() : StringUtil.EMPTY;

					result.put(item.getWord(), path);
				}
			}
		}

		return result;
	}
}
