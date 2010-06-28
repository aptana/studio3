package com.aptana.editor.js.contentassist.index;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.ParameterElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.ReturnTypeElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.editor.js.contentassist.model.UserAgentElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.QueryResult;
import com.aptana.index.core.SearchPattern;

public class JSIndexReader
{
	/**
	 * createProperty
	 * 
	 * @param index
	 * @param key
	 * @return
	 * @throws IOException 
	 */
	protected PropertyElement createPropertyFromKey(Index index, String key) throws IOException
	{
		String[] columns = key.split(JSIndexConstants.DELIMITER);
		int column = 0;
		
		PropertyElement p = new PropertyElement();

		p.setName(columns[column++]);
		column++;	// skip owning type
		p.setDescription(this.getDescription(index, columns[column++]));
		
		for (ReturnTypeElement returnType : this.getReturnTypes(index, columns[column++]))
		{
			p.addType(returnType);
		}
		
		if (column < columns.length)
		{
			for (String userAgentKey : columns[column++].split(JSIndexConstants.SUB_DELIMITER))
			{
				// get user agent and add to element
				p.addUserAgent(this.getUserAgent(index, userAgentKey));
			}
		}
		
		return p;
	}
	
	/**
	 * getDescription
	 * 
	 * @param index
	 * @param descriptionKey
	 * @return
	 * @throws IOException
	 */
	public String getDescription(Index index, String descriptionKey) throws IOException
	{
		String result = ""; //$NON-NLS-1$

		if (descriptionKey != null && descriptionKey.length() > 0 && !descriptionKey.equals(JSIndexConstants.NO_ENTRY))
		{
			// grab description
			String descriptionPattern = descriptionKey + JSIndexConstants.DELIMITER;
			List<QueryResult> descriptions = index.query(new String[] { JSIndexConstants.DESCRIPTION }, descriptionPattern, SearchPattern.PREFIX_MATCH);
	
			if (descriptions != null)
			{
				String descriptionValue = descriptions.get(0).getWord();
	
				result = descriptionValue.substring(descriptionValue.indexOf(JSIndexConstants.DELIMITER) + 1);
			}
		}

		return result;
	}
	
	/**
	 * getFunctions
	 * 
	 * @param index
	 * @param owningType
	 * @return
	 * @throws IOException
	 */
	public List<FunctionElement> getFunctions(Index index, String owningType) throws IOException
	{
		// read properties
		List<QueryResult> functions = index.query(new String[] { JSIndexConstants.FUNCTION }, this.getMemberPattern(owningType), SearchPattern.REGEX_MATCH);
		List<FunctionElement> result = new LinkedList<FunctionElement>();

		if (functions != null)
		{
			for (QueryResult function : functions)
			{
				String[] columns = function.getWord().split(JSIndexConstants.DELIMITER);
				int column = 0;
				
				FunctionElement f = new FunctionElement();

				f.setName(columns[column++]);
				column++;	// skip owning type
				f.setDescription(this.getDescription(index, columns[column++]));
				
				for (ParameterElement parameter : this.getParameters(index, columns[column++]))
				{
					f.addParameter(parameter);
				}

				for (ReturnTypeElement returnType : this.getReturnTypes(index, columns[column++]))
				{
					f.addReturnType(returnType);
				}
				
				if (column < columns.length)
				{
					for (String userAgentKey : columns[column++].split(JSIndexConstants.SUB_DELIMITER))
					{
						// get user agent and add to element
						f.addUserAgent(this.getUserAgent(index, userAgentKey));
					}
				}
				
				result.add(f);
			}
		}

		return result;
	}

	/**
	 * getMemberPattern
	 * 
	 * @param typeName
	 * @return
	 */
	private String getMemberPattern(String typeName)
	{
		return MessageFormat.format("^[^{0}]+{0}{1}(?:{0}|$)", new Object[] { JSIndexConstants.DELIMITER, typeName }); //$NON-NLS-1$
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
		return MessageFormat.format("^{2}{0}{1}(?:{0}|$)", new Object[] { JSIndexConstants.DELIMITER, typeName, memberName }); //$NON-NLS-1$
	}

	/**
	 * getParameters
	 * 
	 * @param index
	 * @param parametersKey
	 * @return
	 * @throws IOException
	 */
	public List<ParameterElement> getParameters(Index index, String parametersKey) throws IOException
	{
		String descriptionPattern = parametersKey + JSIndexConstants.DELIMITER;
		List<QueryResult> parameters = index.query(new String[] { JSIndexConstants.PARAMETERS }, descriptionPattern, SearchPattern.PREFIX_MATCH);
		List<ParameterElement> result = new LinkedList<ParameterElement>();

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

		return result;
	}
	
	/**
	 * getProperties
	 * 
	 * @param index
	 * @param owningType
	 * @return
	 * @throws IOException
	 */
	public List<PropertyElement> getProperties(Index index, String owningType) throws IOException
	{
		// read properties
		List<QueryResult> properties = index.query(new String[] { JSIndexConstants.PROPERTY }, this.getMemberPattern(owningType), SearchPattern.REGEX_MATCH);
		List<PropertyElement> result = new LinkedList<PropertyElement>();

		if (properties != null)
		{
			for (QueryResult property : properties)
			{
				String key = property.getWord();
				PropertyElement p = this.createPropertyFromKey(index, key);

				result.add(p);
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
	 * @return
	 * @throws IOException
	 */
	public PropertyElement getProperty(Index index, String owningType, String propertyName) throws IOException
	{
		List<QueryResult> properties = index.query(new String[] { JSIndexConstants.PROPERTY }, this.getMemberPattern(owningType, propertyName), SearchPattern.REGEX_MATCH);
		PropertyElement result = null;
		
		if (properties != null && properties.size() > 0)
		{
			QueryResult property = properties.get(0);
			String key = property.getWord();
			
			result = this.createPropertyFromKey(index, key);
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
	public List<ReturnTypeElement> getReturnTypes(Index index, String returnTypesKey) throws IOException
	{
		String descriptionPattern = returnTypesKey + JSIndexConstants.DELIMITER;
		List<QueryResult> returnTypes = index.query(new String[] { JSIndexConstants.RETURN_TYPES }, descriptionPattern, SearchPattern.PREFIX_MATCH);
		List<ReturnTypeElement> result = new LinkedList<ReturnTypeElement>();

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

		return result;
	}
	
	/**
	 * getType
	 * 
	 * @param index
	 * @param typeName
	 * @return
	 */
	public TypeElement getType(Index index, String typeName)
	{
		TypeElement result = null;

		try
		{
			String pattern = typeName + JSIndexConstants.DELIMITER;
			List<QueryResult> types = index.query(new String[] { JSIndexConstants.TYPE }, pattern, SearchPattern.PREFIX_MATCH);

			if (types != null && types.size() > 0)
			{
				String[] columns = types.get(0).getWord().split(JSIndexConstants.DELIMITER);
				String retrievedName = columns[0];
				String[] parentTypes = columns[1].split(JSIndexConstants.SUB_DELIMITER); //$NON-NLS-1$
				String descriptionKey = columns[2];

				// create type
				result = new TypeElement();
				
				// set its name
				result.setName(retrievedName);
				
				// add in the types it inherits
				for (String parentType : parentTypes)
				{
					result.addParentType(parentType);
				}
				
				// set the description
				result.setDescription(this.getDescription(index, descriptionKey));

				// add properties
				for (PropertyElement property : this.getProperties(index, retrievedName))
				{
					result.addProperty(property);
				}

				// add functions
				for (FunctionElement function: this.getFunctions(index, retrievedName))
				{
					result.addProperty(function);
				}
			}
		}
		catch (IOException e)
		{
		}

		return result;
	}

	/**
	 * getTypeProperties
	 * 
	 * @return
	 * @throws IOException 
	 */
	public List<PropertyElement> getTypeProperties(Index index, String typeName) throws IOException
	{
		List<PropertyElement> properties = this.getProperties(index, typeName);
		
		properties.addAll(this.getFunctions(index, typeName));
		
		return properties;
	}
	
	/**
	 * getUserAgent
	 * 
	 * @param userAgentKey
	 * @return
	 * @throws IOException
	 */
	protected UserAgentElement getUserAgent(Index index, String userAgentKey) throws IOException
	{
		UserAgentElement result = JSIndexWriter.userAgentsByKey.get(userAgentKey);
		
		if (result == null)
		{
			String searchKey = userAgentKey + JSIndexConstants.DELIMITER;
			List<QueryResult> items = index.query(new String[] { JSIndexConstants.USER_AGENT }, searchKey,
					SearchPattern.PREFIX_MATCH);
	
			if (items != null && items.size() > 0)
			{
				String key = items.get(0).getWord();
				String[] columns = key.split(JSIndexConstants.DELIMITER);
				int column = 1; // skip index
	
				result = new UserAgentElement();
				result.setDescription(columns[column++]);
				result.setOS(columns[column++]);
				result.setPlatform(columns[column++]);
	
				// NOTE: split does not return a final empty element if the string being split
				// ends with the delimiter.
				if (column < columns.length)
				{
					result.setVersion(columns[column++]);
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
	public Map<String, List<String>> getValues(Index index, String category)
	{
		Map<String, List<String>> result = null;

		if (index != null)
		{
			String pattern = "*"; //$NON-NLS-1$

			try
			{
				List<QueryResult> items = index.query(new String[] { category }, pattern, SearchPattern.PATTERN_MATCH);

				if (items != null && items.size() > 0)
				{
					result = new HashMap<String, List<String>>();

					for (QueryResult item : items)
					{
						result.put(item.getWord(), Arrays.asList(item.getDocuments()));
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
