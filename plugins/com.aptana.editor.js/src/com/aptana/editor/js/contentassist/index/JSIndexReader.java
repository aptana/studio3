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
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.js.Activator;
import com.aptana.editor.js.contentassist.JSIndexQueryHelper;
import com.aptana.editor.js.contentassist.model.ContentSelector;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.ParameterElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.ReturnTypeElement;
import com.aptana.editor.js.contentassist.model.SinceElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.editor.js.contentassist.model.UserAgentElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.QueryResult;
import com.aptana.index.core.SearchPattern;

public class JSIndexReader
{
	private static Map<String, UserAgentElement> userAgentsByKey = new HashMap<String, UserAgentElement>();

	/**
	 * createFunctionFromKey
	 * 
	 * @param index
	 * @param key
	 * @param fields
	 * @return
	 * @throws IOException
	 */
	protected FunctionElement createFunction(Index index, QueryResult function, EnumSet<ContentSelector> fields) throws IOException
	{
		FunctionElement f = new FunctionElement();

		if (fields.isEmpty() == false)
		{
			String key = function.getWord();
			String[] columns = key.split(JSIndexConstants.DELIMITER);
			int column = 0;
			
			// owning type
			f.setOwningType(columns[column]);
			column++;

			// name
			if (fields.contains(ContentSelector.NAME))
			{
				f.setName(columns[column]);
			}
			column++;

			// description
			if (fields.contains(ContentSelector.DESCRIPTION))
			{
				f.setDescription(this.getDescription(index, columns[column]));
			}
			column++;

			// types
			if (fields.contains(ContentSelector.TYPES))
			{
				for (ReturnTypeElement returnType : this.getReturnTypes(index, columns[column]))
				{
					f.addType(returnType);
				}
			}
			column++;

			// parameters
			if (fields.contains(ContentSelector.PARAMETERS))
			{
				for (ParameterElement parameter : this.getParameters(index, columns[column]))
				{
					f.addParameter(parameter);
				}
			}
			column++;

			// return types
			if (fields.contains(ContentSelector.RETURN_TYPES))
			{
				for (ReturnTypeElement returnType : this.getReturnTypes(index, columns[column]))
				{
					f.addReturnType(returnType);
				}
			}
			column++;

			// examples
			if (fields.contains(ContentSelector.EXAMPLES))
			{
				for (String example : this.getExamples(index, columns[column]))
				{
					f.addExample(example);
				}
			}
			column++;

			// since list
			if (fields.contains(ContentSelector.SINCE))
			{
				for (SinceElement since : this.getSinceList(index, columns[column]))
				{
					f.addSince(since);
				}
			}
			column++;

			if (column < columns.length)
			{
				// user agents
				if (fields.contains(ContentSelector.USER_AGENTS))
				{
					for (String userAgentKey : columns[column].split(JSIndexConstants.SUB_DELIMITER))
					{
						// get user agent and add to element
						f.addUserAgent(this.getUserAgent(userAgentKey));
					}
				}
				column++;
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
	protected PropertyElement createProperty(Index index, QueryResult property, EnumSet<ContentSelector> fields) throws IOException
	{
		PropertyElement p = new PropertyElement();

		if (fields.isEmpty() == false)
		{
			String key = property.getWord();
			String[] columns = key.split(JSIndexConstants.DELIMITER);
			int column = 0;
			
			// owning type
			p.setOwningType(columns[column]);
			column++;

			// name
			if (fields.contains(ContentSelector.NAME))
			{
				p.setName(columns[column]);
			}
			column++;

			// description
			if (fields.contains(ContentSelector.DESCRIPTION))
			{
				p.setDescription(this.getDescription(index, columns[column]));
			}
			column++;

			// types
			if (fields.contains(ContentSelector.TYPES))
			{
				for (ReturnTypeElement returnType : this.getReturnTypes(index, columns[column]))
				{
					p.addType(returnType);
				}
			}
			column++;

			// examples
			if (fields.contains(ContentSelector.EXAMPLES))
			{
				for (String example : this.getExamples(index, columns[column]))
				{
					p.addExample(example);
				}
			}
			column++;

			// since list
			if (fields.contains(ContentSelector.SINCE))
			{
				for (SinceElement since : this.getSinceList(index, columns[column]))
				{
					p.addSince(since);
				}
			}
			column++;

			if (column < columns.length)
			{
				if (fields.contains(ContentSelector.USER_AGENTS))
				{
					// user agents
					for (String userAgentKey : columns[column].split(JSIndexConstants.SUB_DELIMITER))
					{
						// get user agent and add to element
						p.addUserAgent(this.getUserAgent(userAgentKey));
					}
					column++;
				}
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
	 * createUserAgent
	 * 
	 * @param key
	 * @return
	 */
	protected UserAgentElement createUserAgent(QueryResult userAgent)
	{
		UserAgentElement result = new UserAgentElement();

		String key = userAgent.getWord();
		String[] columns = key.split(JSIndexConstants.DELIMITER);
		int column = 0;

		column++; // skip key
		result.setDescription(columns[column++]);
		result.setOS(columns[column++]);
		result.setPlatform(columns[column++]);

		// NOTE: split does not return a final empty element if the string being split
		// ends with the delimiter.
		if (column < columns.length)
		{
			result.setVersion(columns[column++]);
		}

		return result;
	}

	/**
	 * getDescription
	 * 
	 * @param index
	 * @param descriptionKey
	 * @return
	 * @throws IOException
	 */
	protected String getDescription(Index index, String descriptionKey) throws IOException
	{
		String result = ""; //$NON-NLS-1$

		if (index != null && descriptionKey != null && descriptionKey.length() > 0 && !descriptionKey.equals(JSIndexConstants.NO_ENTRY))
		{
			// grab description
			String descriptionPattern = descriptionKey + JSIndexConstants.DELIMITER;
			List<QueryResult> descriptions = index.query(new String[] { JSIndexConstants.DESCRIPTION }, descriptionPattern, SearchPattern.PREFIX_MATCH
				| SearchPattern.CASE_SENSITIVE);

			if (descriptions != null && descriptions.isEmpty() == false)
			{
				String descriptionValue = descriptions.get(0).getWord();

				result = descriptionValue.substring(descriptionValue.indexOf(JSIndexConstants.DELIMITER) + 1);
			}
		}

		return result;
	}

	/**
	 * getExamples
	 * 
	 * @param index
	 * @param examplesKey
	 * @return
	 * @throws IOException
	 */
	protected List<String> getExamples(Index index, String examplesKey) throws IOException
	{
		List<String> result = new ArrayList<String>();

		if (index != null && examplesKey != null && examplesKey.length() > 0 && !examplesKey.equals(JSIndexConstants.NO_ENTRY))
		{
			// grab description
			String examplePattern = examplesKey + JSIndexConstants.DELIMITER;
			List<QueryResult> queryResult = index.query(new String[] { JSIndexConstants.EXAMPLES }, examplePattern, SearchPattern.PREFIX_MATCH
				| SearchPattern.CASE_SENSITIVE);

			if (queryResult != null && queryResult.size() > 0)
			{
				String word = queryResult.get(0).getWord();
				String[] examples = word.split(JSIndexConstants.DELIMITER);

				for (int i = 1; i < examples.length; i++)
				{
					result.add(examples[i]);
				}
			}
		}

		return result;
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
			List<QueryResult> functions = index.query(new String[] { JSIndexConstants.FUNCTION }, this.getMemberPattern(owningType, propertyName),
				SearchPattern.PREFIX_MATCH | SearchPattern.CASE_SENSITIVE);

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
			List<QueryResult> functions = index.query(new String[] { JSIndexConstants.FUNCTION }, this.getMemberPattern(owningType),
				SearchPattern.PREFIX_MATCH | SearchPattern.CASE_SENSITIVE);

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
	 * getParameters
	 * 
	 * @param index
	 * @param parametersKey
	 * @return
	 * @throws IOException
	 */
	protected List<ParameterElement> getParameters(Index index, String parametersKey) throws IOException
	{
		List<ParameterElement> result = new ArrayList<ParameterElement>();

		if (index != null)
		{
			String descriptionPattern = parametersKey + JSIndexConstants.DELIMITER;
			List<QueryResult> parameters = index.query(new String[] { JSIndexConstants.PARAMETERS }, descriptionPattern, SearchPattern.PREFIX_MATCH
				| SearchPattern.CASE_SENSITIVE);

			if (parameters != null && parameters.size() > 0)
			{
				String parametersValue = parameters.get(0).getWord();
				String[] parameterValues = parametersValue.split(JSIndexConstants.DELIMITER);

				for (int i = 1; i < parameterValues.length; i++)
				{
					String parameterValue = parameterValues[i];
					String[] columns = parameterValue.split(","); //$NON-NLS-1$
					ParameterElement parameter = new ParameterElement();

					parameter.setName(columns[0]);
					parameter.setUsage(columns[1]);

					for (int j = 2; j < columns.length; j++)
					{
						parameter.addType(columns[j]);
					}

					result.add(parameter);
				}
			}
		}

		return result;
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
			List<QueryResult> properties = index.query(new String[] { JSIndexConstants.PROPERTY }, this.getMemberPattern(owningType, propertyName),
				SearchPattern.PREFIX_MATCH | SearchPattern.CASE_SENSITIVE);

			if (properties != null && properties.size() > 0)
			{
				result = this.createProperty(index, properties.get(0), fields);
			}
		}

		return result;
	}

	/**
	 * getReturnTypes
	 * 
	 * @param index
	 * @param returnTypesKey
	 * @return
	 * @throws IOException
	 */
	protected List<ReturnTypeElement> getReturnTypes(Index index, String returnTypesKey) throws IOException
	{
		List<ReturnTypeElement> result = new ArrayList<ReturnTypeElement>();

		if (index != null)
		{
			String descriptionPattern = returnTypesKey + JSIndexConstants.DELIMITER;
			List<QueryResult> returnTypes = index.query(new String[] { JSIndexConstants.RETURN_TYPES }, descriptionPattern, SearchPattern.PREFIX_MATCH
				| SearchPattern.CASE_SENSITIVE);

			if (returnTypes != null && returnTypes.size() > 0)
			{
				String word = returnTypes.get(0).getWord();
				String[] returnTypesValues = word.split(JSIndexConstants.DELIMITER);

				for (int i = 1; i < returnTypesValues.length; i++)
				{
					String returnTypeValue = returnTypesValues[i];
					String[] columns = returnTypeValue.split(","); //$NON-NLS-1$
					ReturnTypeElement returnType = new ReturnTypeElement();

					returnType.setType(columns[0]);
					returnType.setDescription(this.getDescription(index, columns[1]));

					result.add(returnType);
				}
			}
		}

		return result;
	}

	/**
	 * getSinceList
	 * 
	 * @param index
	 * @param sinceListKey
	 * @return
	 * @throws IOException
	 */
	protected List<SinceElement> getSinceList(Index index, String sinceListKey) throws IOException
	{
		List<SinceElement> result = new ArrayList<SinceElement>();

		if (index != null && sinceListKey != null && sinceListKey.length() > 0 && !sinceListKey.equals(JSIndexConstants.NO_ENTRY))
		{
			String descriptionPattern = sinceListKey + JSIndexConstants.DELIMITER;
			List<QueryResult> queryResult = index.query(new String[] { JSIndexConstants.SINCE_LIST }, descriptionPattern, SearchPattern.PREFIX_MATCH
				| SearchPattern.CASE_SENSITIVE);

			if (queryResult != null && queryResult.size() > 0)
			{
				String word = queryResult.get(0).getWord();
				String[] sinceListItems = word.split(JSIndexConstants.DELIMITER);

				for (int i = 1; i < sinceListItems.length; i++)
				{
					String sinceListItem = sinceListItems[i];
					String[] parts = sinceListItem.split(JSIndexConstants.SUB_DELIMITER);
					SinceElement since = new SinceElement();

					since.setName(parts[0]);

					if (parts.length > 1)
					{
						since.setVersion(parts[1]);
					}

					result.add(since);
				}
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
					String[] columns = type.getWord().split(JSIndexConstants.DELIMITER);
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
							for (String parentType : columns[column].split(JSIndexConstants.SUB_DELIMITER))
							{
								result.addParentType(parentType);
							}
						}
						column++;

						// description
						if (fields.contains(ContentSelector.DESCRIPTION))
						{
							result.setDescription(this.getDescription(index, columns[column]));
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
	 * getUserAgent
	 * 
	 * @param userAgentKey
	 * @return
	 * @throws IOException
	 */
	protected UserAgentElement getUserAgent(String userAgentKey) throws IOException
	{
		UserAgentElement result = userAgentsByKey.get(userAgentKey);

		if (result == null)
		{
			Index index = JSIndexQueryHelper.getIndex();
			String searchKey = userAgentKey + JSIndexConstants.DELIMITER;
			List<QueryResult> items = index.query(new String[] { JSIndexConstants.USER_AGENT }, searchKey, SearchPattern.PREFIX_MATCH);

			if (items != null && items.size() > 0)
			{
				result = this.createUserAgent(items.get(0));

				if (result != null)
				{
					userAgentsByKey.put(userAgentKey, result);
				}
			}
		}

		return result;
	}

	/**
	 * getUserAgents
	 * 
	 * @return
	 * @throws IOException
	 */
	public List<UserAgentElement> getUserAgents() throws IOException
	{
		List<UserAgentElement> result = Collections.emptyList();
		Index index = JSIndexQueryHelper.getIndex();
		List<QueryResult> items = index.query(new String[] { JSIndexConstants.USER_AGENT }, "*", SearchPattern.PATTERN_MATCH); //$NON-NLS-1$

		if (items != null && items.isEmpty() == false)
		{
			result = new ArrayList<UserAgentElement>();

			for (QueryResult item : items)
			{
				result.add(this.createUserAgent(item));
			}
		}

		return result;
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

	/**
	 * getValues
	 * 
	 * @return
	 */
	public Map<String, Collection<String>> getValues(Index index, String category)
	{
		Map<String, Collection<String>> result = null;

		if (index != null)
		{
			String pattern = "*"; //$NON-NLS-1$

			try
			{
				List<QueryResult> items = index.query(new String[] { category }, pattern, SearchPattern.PATTERN_MATCH);

				if (items != null && items.size() > 0)
				{
					result = new HashMap<String, Collection<String>>();

					for (QueryResult item : items)
					{
						result.put(item.getWord(), item.getDocuments());
					}
				}
			}
			catch (IOException e)
			{
				Activator.logError(e.getMessage(), e);
			}
		}

		return result;
	}
}
