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
package com.aptana.index.core;

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

		return element;
	}
}
