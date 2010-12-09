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
package com.aptana.editor.js.contentassist.index;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.mortbay.util.ajax.JSON;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.js.contentassist.model.ContentSelector;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.QueryResult;
import com.aptana.index.core.SearchPattern;

public class JSIndexReader
{
	private static final Pattern DELIMITER_PATTERN = Pattern.compile(JSIndexConstants.DELIMITER);
	private static final Pattern SUB_DELIMETER_PATTERN = Pattern.compile(JSIndexConstants.SUB_DELIMITER);

	/**
	 * createFunction
	 * 
	 * @param index
	 * @param function
	 * @param fields
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	protected FunctionElement createFunction(Index index, QueryResult function, EnumSet<ContentSelector> fields) throws IOException
	{
		FunctionElement f = new FunctionElement();

		if (fields.isEmpty() == false)
		{
			String key = function.getWord();
			String[] columns = DELIMITER_PATTERN.split(key);

			Object m = JSON.parse(columns[2]);

			if (m instanceof Map)
			{
				f.fromJSON((Map) m);
			}

			// documents
			if (fields.contains(ContentSelector.DOCUMENTS))
			{
				for (String document : function.getDocuments())
				{
					f.addDocument(document);
				}
			}
		}

		return f;
	}

	/**
	 * createProperty
	 * 
	 * @param index
	 * @param key
	 * @param fields
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	protected PropertyElement createProperty(Index index, QueryResult property, EnumSet<ContentSelector> fields) throws IOException
	{
		PropertyElement p = new PropertyElement();

		if (fields.isEmpty() == false)
		{
			String key = property.getWord();
			String[] columns = DELIMITER_PATTERN.split(key);

			Object m = JSON.parse(columns[2]);

			if (m instanceof Map)
			{
				p.fromJSON((Map) m);
			}

			// documents
			if (fields.contains(ContentSelector.DOCUMENTS))
			{
				for (String document : property.getDocuments())
				{
					p.addDocument(document);
				}
			}
		}

		return p;
	}

	/**
	 * getFunction
	 * 
	 * @param index
	 * @param owningType
	 * @param propertyName
	 * @param fields
	 * @return
	 * @throws IOException
	 */
	public FunctionElement getFunction(Index index, String owningType, String propertyName, EnumSet<ContentSelector> fields) throws IOException
	{
		FunctionElement result = null;

		if (index != null)
		{
			List<QueryResult> functions = index.query( //
				new String[] { JSIndexConstants.FUNCTION }, //
				this.getMemberPattern(owningType, propertyName), //
				SearchPattern.PREFIX_MATCH | SearchPattern.CASE_SENSITIVE //
			);

			if (functions != null && functions.size() > 0)
			{
				result = this.createFunction(index, functions.get(0), fields);
			}
		}

		return result;
	}

	/**
	 * getFunctions
	 * 
	 * @param index
	 * @param owningTypes
	 * @param fields
	 * @return
	 * @throws IOException
	 */
	public List<FunctionElement> getFunctions(Index index, List<String> owningTypes, EnumSet<ContentSelector> fields) throws IOException
	{
		List<FunctionElement> result = new ArrayList<FunctionElement>();

		if (index != null && owningTypes != null && owningTypes.isEmpty() == false)
		{
			// read functions
			List<QueryResult> functions = index
				.query(new String[] { JSIndexConstants.FUNCTION }, this.getMemberPattern(owningTypes), SearchPattern.REGEX_MATCH);

			if (functions != null)
			{
				for (QueryResult function : functions)
				{
					result.add(this.createFunction(index, function, fields));
				}
			}
		}

		return result;
	}

	/**
	 * getFunctions
	 * 
	 * @param index
	 * @param owningType
	 * @param fields
	 * @return
	 * @throws IOException
	 */
	public List<FunctionElement> getFunctions(Index index, String owningType, EnumSet<ContentSelector> fields) throws IOException
	{
		List<FunctionElement> result = new ArrayList<FunctionElement>();

		if (index != null && owningType != null && owningType.length() > 0)
		{
			// read functions
			List<QueryResult> functions = index.query(new String[] { JSIndexConstants.FUNCTION }, this.getMemberPattern(owningType), SearchPattern.PREFIX_MATCH
				| SearchPattern.CASE_SENSITIVE);

			if (functions != null)
			{
				for (QueryResult function : functions)
				{
					result.add(this.createFunction(index, function, fields));
				}
			}
		}

		return result;
	}

	/**
	 * getMemberPattern
	 * 
	 * @param typeNames
	 * @return
	 */
	private String getMemberPattern(List<String> typeNames)
	{
		String typePattern = getUserTypesPattern(typeNames);

		return MessageFormat.format("^{1}{0}", new Object[] { JSIndexConstants.DELIMITER, typePattern }); //$NON-NLS-1$
	}

	/**
	 * getMemberPattern
	 * 
	 * @param typeName
	 * @return
	 */
	private String getMemberPattern(String typeName)
	{
		return MessageFormat.format("{1}{0}", new Object[] { JSIndexConstants.DELIMITER, typeName }); //$NON-NLS-1$
	}

	/**
	 * getMemberPattern
	 * 
	 * @param typeName
	 * @param memberName
	 * @return
	 */
	private String getMemberPattern(String typeName, String memberName)
	{
		return MessageFormat.format("{1}{0}{2}", new Object[] { JSIndexConstants.DELIMITER, typeName, memberName }); //$NON-NLS-1$
	}

	/**
	 * getProperties
	 * 
	 * @param index
	 * @param owningTypes
	 * @param fields
	 * @return
	 * @throws IOException
	 */
	public List<PropertyElement> getProperties(Index index, List<String> owningTypes, EnumSet<ContentSelector> fields) throws IOException
	{
		List<PropertyElement> result = new ArrayList<PropertyElement>();

		if (index != null && owningTypes != null && owningTypes.isEmpty() == false)
		{
			// read properties
			List<QueryResult> properties = index.query(new String[] { JSIndexConstants.PROPERTY }, this.getMemberPattern(owningTypes),
				SearchPattern.REGEX_MATCH);

			if (properties != null)
			{
				for (QueryResult property : properties)
				{
					result.add(this.createProperty(index, property, fields));
				}
			}
		}

		return result;
	}

	/**
	 * getProperties
	 * 
	 * @param index
	 * @param owningType
	 * @param fields
	 * @return
	 * @throws IOException
	 */
	public List<PropertyElement> getProperties(Index index, String owningType, EnumSet<ContentSelector> fields) throws IOException
	{
		List<PropertyElement> result = new ArrayList<PropertyElement>();

		if (index != null && owningType != null && owningType.length() > 0)
		{
			// read properties
			List<QueryResult> properties = index.query(new String[] { JSIndexConstants.PROPERTY }, this.getMemberPattern(owningType),
				SearchPattern.PREFIX_MATCH | SearchPattern.CASE_SENSITIVE);

			if (properties != null)
			{
				for (QueryResult property : properties)
				{
					result.add(this.createProperty(index, property, fields));
				}
			}
		}

		return result;
	}

	/**
	 * getProperty
	 * 
	 * @param index
	 * @param owningType
	 * @param propertyName
	 * @param fields
	 * @return
	 * @throws IOException
	 */
	public PropertyElement getProperty(Index index, String owningType, String propertyName, EnumSet<ContentSelector> fields) throws IOException
	{
		PropertyElement result = null;

		if (index != null)
		{
			List<QueryResult> properties = index.query( //
				new String[] { JSIndexConstants.PROPERTY }, //
				this.getMemberPattern(owningType, propertyName), //
				SearchPattern.PREFIX_MATCH | SearchPattern.CASE_SENSITIVE //
			);

			if (properties != null && properties.size() > 0)
			{
				result = this.createProperty(index, properties.get(0), fields);
			}
		}

		return result;
	}

	/**
	 * getType
	 * 
	 * @param index
	 * @param typeName
	 * @return
	 */
	public TypeElement getType(Index index, String typeName, EnumSet<ContentSelector> fields)
	{
		TypeElement result = null;

		if (index != null)
		{
			try
			{
				String pattern = typeName + JSIndexConstants.DELIMITER;
				List<QueryResult> types = index.query(new String[] { JSIndexConstants.TYPE }, pattern, SearchPattern.PREFIX_MATCH);

				if (types != null && types.size() > 0)
				{
					QueryResult type = types.get(0);
					String[] columns = DELIMITER_PATTERN.split(type.getWord());
					String retrievedName = columns[0];
					int column = 0;

					// create type
					result = new TypeElement();

					if (fields.isEmpty() == false)
					{
						// name
						if (fields.contains(ContentSelector.NAME))
						{
							result.setName(columns[column]);
						}
						column++;

						// super types
						if (fields.contains(ContentSelector.PARENT_TYPES))
						{
							for (String parentType : SUB_DELIMETER_PATTERN.split(columns[column]))
							{
								result.addParentType(parentType);
							}
						}
						column++;

						// description
						if (column < columns.length && fields.contains(ContentSelector.DESCRIPTION))
						{
							result.setDescription(columns[column]);
						}
						column++;

						// properties
						if (fields.contains(ContentSelector.PROPERTIES))
						{
							for (PropertyElement property : this.getProperties(index, retrievedName, EnumSet.allOf(ContentSelector.class)))
							{
								result.addProperty(property);
							}
						}

						// functions
						if (fields.contains(ContentSelector.FUNCTIONS))
						{
							for (FunctionElement function : this.getFunctions(index, retrievedName, EnumSet.allOf(ContentSelector.class)))
							{
								result.addProperty(function);
							}
						}

						// documents
						if (fields.contains(ContentSelector.DOCUMENTS))
						{
							for (String document : type.getDocuments())
							{
								result.addDocument(document);
							}
						}
					}
				}
			}
			catch (IOException e)
			{
			}
		}

		return result;
	}

	/**
	 * getTypeProperties
	 * 
	 * @param index
	 * @param typeName
	 * @param fields
	 * @return
	 * @throws IOException
	 */
	public List<PropertyElement> getTypeProperties(Index index, String typeName, EnumSet<ContentSelector> fields) throws IOException
	{
		List<PropertyElement> properties = this.getProperties(index, typeName, fields);

		properties.addAll(this.getFunctions(index, typeName, fields));

		return properties;
	}

	/**
	 * getUserTypesPattern
	 * 
	 * @param owningTypes
	 * @return
	 */
	protected String getUserTypesPattern(List<String> owningTypes)
	{
		List<String> quotedOwningTypes = new ArrayList<String>(owningTypes.size());

		// escape each owning type
		for (String owningType : owningTypes)
		{
			quotedOwningTypes.add(Pattern.quote(owningType));
		}

		// build pattern for all types
		return "(" + StringUtil.join("|", quotedOwningTypes) + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}
