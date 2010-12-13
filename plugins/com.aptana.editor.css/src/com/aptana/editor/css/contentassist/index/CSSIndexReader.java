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

import com.aptana.core.util.StringUtil;
import com.aptana.editor.css.contentassist.model.ElementElement;
import com.aptana.editor.css.contentassist.model.PropertyElement;
import com.aptana.editor.css.contentassist.model.PseudoClassElement;
import com.aptana.editor.css.contentassist.model.PseudoElementElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexReader;
import com.aptana.index.core.QueryResult;
import com.aptana.index.core.SearchPattern;

public class CSSIndexReader extends IndexReader
{
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
		return CSSIndexConstants.DELIMITER;
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
					result.add(this.createElement(element));
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
					new String[] { CSSIndexConstants.PROPERTY }, //
					name + CSSIndexConstants.DELIMITER, //
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
				new String[] { CSSIndexConstants.PSUEDO_CLASS }, //
				"*", //$NON-NLS-1$
				SearchPattern.PATTERN_MATCH //
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
				new String[] { CSSIndexConstants.PSUEDO_ELEMENT }, //
				"*", //$NON-NLS-1$
				SearchPattern.PATTERN_MATCH //
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
		return CSSIndexConstants.SUB_DELIMITER;
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
