/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.index.core;

import java.text.MessageFormat;
import java.util.Map;
import java.util.regex.Pattern;

import org.mortbay.util.ajax.JSON;
import org.mortbay.util.ajax.JSON.Convertible;

/**
 * IndexReader
 */
public abstract class IndexReader
{
	public static Pattern DELIMITER_PATTERN;
	public static Pattern SUB_DELIMITER_PATTERN;

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
			this.populateElement(element, item.getWord());
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
				this.populateElement(element, columns[columnIndex]);
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
	private <T extends Convertible & IndexDocument> T populateElement(T element, String value)
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

				for (String document : element.getDocuments())
				{
					element.addDocument(document);
				}
			}
			catch (Throwable t)
			{
				String message = MessageFormat.format( //
					"An error occurred while processing the following JSON string\n{0}", //
					value //
					);

				IndexPlugin.logError(message, t);
			}
		}

		return element;
	}
}
