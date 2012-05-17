/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.index.core;

import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.aptana.jetty.util.epl.ajax.JSON;
import com.aptana.jetty.util.epl.ajax.JSON.Convertible;

import com.aptana.core.IFilter;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;

/**
 * IndexReader
 */
public abstract class IndexReader
{
	public static Pattern DELIMITER_PATTERN;
	public static Pattern SUB_DELIMITER_PATTERN;

	/**
	 * getCategoryInfo
	 * 
	 * @param category
	 * @return
	 */
	public CategoryInfo getCategoryInfo(Index index, String category)
	{
		List<Integer> lengths = new ArrayList<Integer>();

		if (index != null)
		{
			// @formatter:off
			List<QueryResult> types = index.query(
				new String[] { category },
				"*", //$NON-NLS-1$
				SearchPattern.PATTERN_MATCH
			);
			// @formatter:on

			if (types != null)
			{
				for (QueryResult query : types)
				{
					String word = query.getWord();
					int length = (word != null) ? word.length() : 0;

					lengths.add(length);
				}
			}
		}

		return new CategoryInfo(category, lengths);
	}

	/**
	 * Get the top-level delimiter string used to separate columns in an index word
	 * 
	 * @return
	 */
	protected abstract String getDelimiter();

	/**
	 * Get a Pattern of the top-level delimiter used to separate index word columns
	 * 
	 * @return
	 */
	protected Pattern getDelimiterPattern()
	{
		if (DELIMITER_PATTERN == null)
		{
			DELIMITER_PATTERN = Pattern.compile(this.getDelimiter());
		}

		return DELIMITER_PATTERN;
	}

	/**
	 * Filter the list of query results to include items coming from the specified location only. Note that a null
	 * location will cause the specified list to be returned
	 * 
	 * @param results
	 *            A list of QueryResults
	 * @param location
	 *            The location URI used to filter the query result list
	 * @return
	 */
	protected List<QueryResult> getQueryResultsForLocation(List<QueryResult> results, final URI location)
	{
		if (location == null)
		{
			return results;
		}

		return CollectionsUtil.filter(results, new IFilter<QueryResult>()
		{
			public boolean include(QueryResult item)
			{
				boolean result = true;

				if (location != null)
				{
					result = item.getDocuments().contains(location.toString());
				}

				return result;
			}
		});
	}

	/**
	 * Get the second level delimiter string used to separate test within a column in an index word
	 * 
	 * @return
	 */
	protected abstract String getSubDelimiter();

	/**
	 * Get a Pattern of the top-level delimiter used to separate index word columns
	 * 
	 * @return
	 */
	protected Pattern getSubDelimiterPattern()
	{
		if (SUB_DELIMITER_PATTERN == null)
		{
			SUB_DELIMITER_PATTERN = Pattern.compile(this.getSubDelimiter());
		}

		return SUB_DELIMITER_PATTERN;
	}

	/**
	 * populateElement
	 * 
	 * @param element
	 * @param item
	 * @param <T>
	 * @return
	 */
	protected <T extends Convertible & IndexDocument> T populateElement(T element, QueryResult item)
	{
		if (item != null && element != null)
		{
			this.populateElement(element, item.getWord(), item.getDocuments());
		}

		return element;
	}

	/**
	 * populateElement
	 * 
	 * @param element
	 * @param item
	 * @param columnIndex
	 * @param <T>
	 * @return
	 */
	protected <T extends Convertible & IndexDocument> T populateElement(T element, QueryResult item, int columnIndex)
	{
		if (item != null && element != null && 0 <= columnIndex)
		{
			String key = item.getWord();
			String[] columns = this.getDelimiterPattern().split(key);

			if (columnIndex < columns.length)
			{
				this.populateElement(element, columns[columnIndex], item.getDocuments());
			}
		}

		return element;
	}

	/**
	 * populateElement
	 * 
	 * @param <T>
	 * @param element
	 * @param value
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private <T extends Convertible & IndexDocument> T populateElement(T element, String value, Set<String> documents)
	{
		if (element != null && value != null)
		{
			try
			{
				Object m = JSON.parse(value);

				if (m instanceof Map)
				{
					element.fromJSON((Map) m);
				}

				for (String document : documents)
				{
					element.addDocument(document);
				}
			}
			catch (Throwable t)
			{
				// @formatter:off
				String message = MessageFormat.format(
					"An error occurred while processing the following JSON string\n{0}", // //$NON-NLS-1$
					value
				);
				// @formatter:on

				IdeLog.logError(IndexPlugin.getDefault(), message, t);
			}
		}

		return element;
	}
}
