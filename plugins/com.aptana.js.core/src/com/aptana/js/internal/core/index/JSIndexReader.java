/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.index;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import com.aptana.core.IMap;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.RegexUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexReader;
import com.aptana.index.core.QueryResult;
import com.aptana.index.core.SearchPattern;
import com.aptana.js.core.JSTypeConstants;
import com.aptana.js.core.index.IJSIndexConstants;
import com.aptana.js.core.model.EventElement;
import com.aptana.js.core.model.FunctionElement;
import com.aptana.js.core.model.PropertyElement;
import com.aptana.js.core.model.TypeElement;
import com.aptana.js.core.model.UserAgentElement;

// TODO Combine with JSIndexQueryHelper! They're both doing the same thing, but Query helper adds the JS Core index in to calls!
public class JSIndexReader extends IndexReader
{
	/**
	 * attachMembers
	 * 
	 * @param type
	 * @param index
	 * @throws IOException
	 */
	protected void attachMembers(TypeElement type, Index index)
	{
		// members
		if (type != null && index != null)
		{
			String typeName = type.getName();

			// properties
			for (PropertyElement property : this.getProperties(index, typeName))
			{
				type.addProperty(property);
			}

			// functions
			for (FunctionElement function : this.getFunctions(index, typeName))
			{
				type.addProperty(function);
			}

			// events
			for (EventElement event : this.getEvents(index, typeName))
			{
				type.addEvent(event);
			}
		}
	}

	/**
	 * createEvent
	 * 
	 * @param event
	 * @return
	 */
	protected EventElement createEvent(QueryResult event)
	{
		return this.populateElement(new EventElement(), event, 2);
	}

	/**
	 * createFunction
	 * 
	 * @param function
	 * @return
	 */
	protected FunctionElement createFunction(QueryResult function)
	{
		return this.populateElement(new FunctionElement(), function, 2);
	}

	/**
	 * createProperty
	 * 
	 * @param property
	 * @return
	 */
	protected PropertyElement createProperty(QueryResult property)
	{
		return this.populateElement(new PropertyElement(), property, 2);
	}

	/**
	 * createType
	 * 
	 * @param type
	 * @return
	 * @throws IOException
	 */
	protected TypeElement createType(QueryResult type)
	{
		TypeElement result;
		String[] columns = this.getDelimiterPattern().split(type.getWord());
		int column = 0;

		// create type
		result = new TypeElement();

		// name
		result.setName(columns[column]);
		column++;

		// super types
		if (column < columns.length)
		{
			for (String parentType : this.getSubDelimiterPattern().split(columns[column]))
			{
				result.addParentType(parentType);
			}
		}
		column++;

		// description
		if (column < columns.length)
		{
			result.setDescription(columns[column]);
		}
		column++;

		// deprecated
		if (column < columns.length)
		{
			result.setIsDeprecated(columns[column].equals("1")); //$NON-NLS-1$
		}
		column++;

		// isInternal
		if (column < columns.length)
		{
			result.setIsInternal(columns[column].equals("1")); //$NON-NLS-1$
		}
		column++;

		// user agents
		if (column < columns.length)
		{
			String value = columns[column];
			if (IJSIndexConstants.ALL_AGENTS.equals(value))
			{
				result.setHasAllUserAgents();
			}
			else
			{
				String[] agents = value.split(IJSIndexConstants.SUB_DELIMITER);
				for (String agent : agents)
				{
					UserAgentElement uaElement = new UserAgentElement();
					uaElement.setPlatform(agent);
					result.addUserAgent(uaElement);
				}
			}
		}
		column++;

		// documents
		for (String document : type.getDocuments())
		{
			result.addDocument(document);
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
		return IJSIndexConstants.DELIMITER;
	}

	/**
	 * getEvents
	 * 
	 * @param index
	 * @param owningTypes
	 * @return
	 * @throws IOException
	 */
	public List<EventElement> getEvents(Index index, List<String> owningTypes)
	{
		if (index != null && !CollectionsUtil.isEmpty(owningTypes))
		{
			// read events
			// @formatter:off
			List<QueryResult> events = index.query(
				new String[] { IJSIndexConstants.EVENT },
				this.getMemberPattern(owningTypes),
				SearchPattern.REGEX_MATCH | SearchPattern.CASE_SENSITIVE
			);
			// @formatter:on

			return CollectionsUtil.map(events, new IMap<QueryResult, EventElement>()
			{
				public EventElement map(QueryResult item)
				{
					return createEvent(item);
				}
			});
		}

		return Collections.emptyList();
	}

	/**
	 * getEvents
	 * 
	 * @param index
	 * @param owningType
	 * @return
	 * @throws IOException
	 */
	public List<EventElement> getEvents(Index index, String owningType)
	{
		return getEvents(index, CollectionsUtil.newList(owningType));
	}

	/**
	 * getEvents
	 * 
	 * @param index
	 * @param owningType
	 * @param eventName
	 * @return
	 * @throws IOException
	 */
	public List<EventElement> getEvents(Index index, String owningType, String eventName)
	{
		if (index != null && !StringUtil.isEmpty(owningType) && !StringUtil.isEmpty(eventName))
		{
			// read events
			// @formatter:off
			List<QueryResult> events = index.query(
				new String[] { IJSIndexConstants.EVENT },
				this.getMemberPattern(owningType, eventName),
				SearchPattern.PREFIX_MATCH | SearchPattern.CASE_SENSITIVE
			);
			// @formatter:on

			return CollectionsUtil.map(events, new IMap<QueryResult, EventElement>()
			{
				public EventElement map(QueryResult item)
				{
					return createEvent(item);
				}
			});
		}

		return Collections.emptyList();
	}

	/**
	 * getFunctions
	 * 
	 * @param index
	 * @param owningTypes
	 * @return
	 * @throws IOException
	 */
	public List<FunctionElement> getFunctions(Index index, List<String> owningTypes)
	{
		if (index != null && !CollectionsUtil.isEmpty(owningTypes))
		{
			// read functions
			// @formatter:off
			List<QueryResult> functions = index.query(
				new String[] { IJSIndexConstants.FUNCTION },
				this.getMemberPattern(owningTypes),
				SearchPattern.REGEX_MATCH | SearchPattern.CASE_SENSITIVE
			);
			// @formatter:on

			return CollectionsUtil.map(functions, new IMap<QueryResult, FunctionElement>()
			{
				public FunctionElement map(QueryResult item)
				{
					return createFunction(item);
				}
			});
		}

		return Collections.emptyList();
	}

	/**
	 * getFunctions
	 * 
	 * @param index
	 * @param owningType
	 * @return
	 * @throws IOException
	 */
	public List<FunctionElement> getFunctions(Index index, String owningType)
	{
		return getFunctions(index, CollectionsUtil.newList(owningType));
	}

	/**
	 * getFunction
	 * 
	 * @param index
	 * @param owningType
	 * @param propertyName
	 * @return
	 * @throws IOException
	 */
	public List<FunctionElement> getFunctions(Index index, String owningType, String propertyName)
	{
		if (index != null && !StringUtil.isEmpty(owningType) && !StringUtil.isEmpty(propertyName))
		{
			// @formatter:off
			List<QueryResult> functions = index.query(
				new String[] { IJSIndexConstants.FUNCTION },
				this.getMemberPattern(owningType, propertyName),
				SearchPattern.PREFIX_MATCH | SearchPattern.CASE_SENSITIVE
			);
			// @formatter:on

			return CollectionsUtil.map(functions, new IMap<QueryResult, FunctionElement>()
			{
				public FunctionElement map(QueryResult item)
				{
					return createFunction(item);
				}
			});
		}

		return Collections.emptyList();
	}

	/**
	 * getMemberPattern
	 * 
	 * @param typeNames
	 * @return
	 */
	private String getMemberPattern(List<String> typeNames)
	{
		typeNames = CollectionsUtil.map(typeNames, new IMap<String, String>()
		{
			public String map(String item)
			{
				return stripGenericsFromType(item);
			}
		});
		String typePattern = RegexUtil.createQuotedListPattern(typeNames);

		return MessageFormat.format("^{1}{0}", new Object[] { this.getDelimiter(), typePattern }); //$NON-NLS-1$
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
		return MessageFormat.format(
				"{1}{0}{2}{0}", new Object[] { this.getDelimiter(), stripGenericsFromType(typeName), memberName }); //$NON-NLS-1$
	}

	/**
	 * Looks for Array<?> and removes the type information for members.
	 * 
	 * @param typeName
	 * @return
	 */
	private String stripGenericsFromType(String typeName)
	{
		if (typeName.startsWith(JSTypeConstants.GENERIC_ARRAY_OPEN))
		{
			return JSTypeConstants.ARRAY_TYPE;
		}
		return typeName;
	}

	/**
	 * getProperties
	 * 
	 * @param index
	 * @param owningTypes
	 * @return
	 * @throws IOException
	 */
	public List<PropertyElement> getProperties(Index index, List<String> owningTypes)
	{
		if (index != null && !CollectionsUtil.isEmpty(owningTypes))
		{
			// read properties
			// @formatter:off
			List<QueryResult> properties = index.query(
				new String[] { IJSIndexConstants.PROPERTY },
				this.getMemberPattern(owningTypes),
				SearchPattern.REGEX_MATCH | SearchPattern.CASE_SENSITIVE
			);
			// @formatter:on

			return CollectionsUtil.map(properties, new IMap<QueryResult, PropertyElement>()
			{
				public PropertyElement map(QueryResult item)
				{
					return createProperty(item);
				}
			});
		}

		return Collections.emptyList();
	}

	/**
	 * getProperties
	 * 
	 * @param index
	 * @param owningType
	 * @return
	 * @throws IOException
	 */
	public List<PropertyElement> getProperties(Index index, String owningType)
	{
		return getProperties(index, CollectionsUtil.newList(owningType));
	}

	/**
	 * getProperty
	 * 
	 * @param index
	 * @param owningType
	 * @param propertyName
	 * @return
	 * @throws IOException
	 */
	public List<PropertyElement> getProperties(Index index, String owningType, String propertyName)
	{
		if (index != null && !StringUtil.isEmpty(owningType) && !StringUtil.isEmpty(propertyName))
		{
			// @formatter:off
			List<QueryResult> properties = index.query(
				new String[] { IJSIndexConstants.PROPERTY },
				this.getMemberPattern(owningType, propertyName),
				SearchPattern.PREFIX_MATCH | SearchPattern.CASE_SENSITIVE
			);
			// @formatter:on

			return CollectionsUtil.map(properties, new IMap<QueryResult, PropertyElement>()
			{
				public PropertyElement map(QueryResult item)
				{
					return createProperty(item);
				}
			});
		}

		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.IndexReader#getSubDelimiter()
	 */
	@Override
	protected String getSubDelimiter()
	{
		return IJSIndexConstants.SUB_DELIMITER;
	}

	/**
	 * getType
	 * 
	 * @param index
	 * @param typeName
	 * @param includeMembers
	 * @return
	 */
	public List<TypeElement> getType(Index index, String typeName, boolean includeMembers)
	{
		List<TypeElement> result = new ArrayList<TypeElement>();

		if (index != null && !StringUtil.isEmpty(typeName))
		{
			String pattern = stripGenericsFromType(typeName) + this.getDelimiter();

			// @formatter:off
			List<QueryResult> types = index.query(
				new String[] { IJSIndexConstants.TYPE },
				pattern,
				SearchPattern.PREFIX_MATCH | SearchPattern.CASE_SENSITIVE
			);
			// @formatter:on

			if (types != null)
			{
				for (QueryResult type : types)
				{
					TypeElement t = this.createType(type);

					if (includeMembers)
					{
						this.attachMembers(t, index);

						// make sure the newly created type can be serialized back to JSON in case it is modified
						t.setSerializeProperties(true);
					}

					result.add(t);
				}
			}
		}

		return result;
	}

	/**
	 * getTypeNames
	 * 
	 * @param index
	 * @return
	 */
	public List<String> getTypeNames(Index index)
	{
		return getTypeNames(index, "*", SearchPattern.PATTERN_MATCH); //$NON-NLS-1$
	}

	/**
	 * getTypes
	 * 
	 * @param index
	 * @param includeMembers
	 * @return
	 * @throws IOException
	 */
	public List<TypeElement> getTypes(Index index, boolean includeMembers)
	{
		List<TypeElement> result = Collections.emptyList();

		if (index != null)
		{
			// @formatter:off
			List<QueryResult> types = index.query(
				new String[] { IJSIndexConstants.TYPE },
				"*", //$NON-NLS-1$
				SearchPattern.PATTERN_MATCH
			);
			// @formatter:on

			if (types != null)
			{
				result = new ArrayList<TypeElement>();

				for (QueryResult type : types)
				{
					TypeElement t = this.createType(type);

					if (includeMembers)
					{
						this.attachMembers(t, index);

						t.setSerializeProperties(true);
					}

					result.add(t);
				}
			}
		}

		return result;
	}

	/**
	 * Convert a list of types into a regular expression. Note that this method assumes that list is non-empty
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

	public List<String> getTypeNames(Index index, String pattern, int matchFlags)
	{
		List<String> result = new ArrayList<String>();

		if (index != null)
		{
			// @formatter:off
			List<QueryResult> types = index.query(
				new String[] { IJSIndexConstants.TYPE },
				pattern, matchFlags
			);
			// @formatter:on

			if (types != null)
			{
				for (QueryResult type : types)
				{
					String word = type.getWord();
					int delimiterIndex = word.indexOf(getDelimiter());

					if (delimiterIndex != -1)
					{
						result.add(new String(word.substring(0, delimiterIndex)));
					}
					// else warn?
				}
			}
		}

		return result;
	}
}
