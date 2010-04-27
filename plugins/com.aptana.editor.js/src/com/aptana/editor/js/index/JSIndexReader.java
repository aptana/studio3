package com.aptana.editor.js.index;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

import com.aptana.editor.js.model.FunctionElement;
import com.aptana.editor.js.model.ParameterElement;
import com.aptana.editor.js.model.PropertyElement;
import com.aptana.editor.js.model.TypeElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.QueryResult;
import com.aptana.index.core.SearchPattern;

public class JSIndexReader
{
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
		String result = "";

		// grab description
		String descriptionPattern = descriptionKey + IndexConstants.DELIMITER;
		List<QueryResult> descriptions = index.query(new String[] { IndexConstants.DESCRIPTION }, descriptionPattern, SearchPattern.PREFIX_MATCH);

		if (descriptions != null)
		{
			String descriptionValue = descriptions.get(0).getWord();

			result = descriptionValue.substring(descriptionValue.indexOf(IndexConstants.DELIMITER) + 1);
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
		List<QueryResult> functions = index.query(new String[] { IndexConstants.FUNCTION }, this.getMemberPattern(owningType), SearchPattern.REGEX_MATCH);
		List<FunctionElement> result = new LinkedList<FunctionElement>();

		if (functions != null)
		{
			for (QueryResult function : functions)
			{
				String[] columns = function.getWord().split(IndexConstants.DELIMITER);
				String functionName = columns[0];
				String descriptionKey = columns[2];
				List<ParameterElement> parameters = this.getParameters(index, columns[3]);
				
				FunctionElement f = new FunctionElement();

				f.setName(functionName);
				f.setDescription(this.getDescription(index, descriptionKey));
				
				for (ParameterElement parameter : parameters)
				{
					f.addParameter(parameter);
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
		return MessageFormat.format("^[^{0}]+{0}{1}(?:{0}|$)", new Object[] { IndexConstants.DELIMITER, typeName });
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
		List<QueryResult> properties = index.query(new String[] { IndexConstants.PROPERTY }, this.getMemberPattern(owningType), SearchPattern.REGEX_MATCH);
		List<PropertyElement> result = new LinkedList<PropertyElement>();

		if (properties != null)
		{
			for (QueryResult property : properties)
			{
				String[] columns = property.getWord().split(IndexConstants.DELIMITER);
				String propertyName = columns[0];
				String descriptionKey = columns[2];
				PropertyElement p = new PropertyElement();

				p.setName(propertyName);
				p.setDescription(this.getDescription(index, descriptionKey));

				result.add(p);
			}
		}

		return result;
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
		String descriptionPattern = parametersKey + IndexConstants.DELIMITER;
		List<QueryResult> parameters = index.query(new String[] { IndexConstants.PARAMETERS }, descriptionPattern, SearchPattern.PREFIX_MATCH);
		List<ParameterElement> result = new LinkedList<ParameterElement>();

		if (parameters != null && parameters.size() > 0)
		{
			String parametersValue = parameters.get(0).getWord();
			String[] parameterValues = parametersValue.split(IndexConstants.DELIMITER);

			for (int i = 1; i < parameterValues.length; i++)
			{
				String parameterValue = parameterValues[i];
				String[] columns = parameterValue.split(",");
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
	 * readType
	 * 
	 * @param index
	 * @param typeName
	 * @return
	 */
	public TypeElement loadType(Index index, String typeName)
	{
		TypeElement result = null;

		try
		{
			String pattern = typeName + IndexConstants.DELIMITER;
			List<QueryResult> types = index.query(new String[] { IndexConstants.TYPE }, pattern, SearchPattern.PREFIX_MATCH);

			if (types != null && types.size() > 0)
			{
				String[] columns = types.get(0).getWord().split(IndexConstants.DELIMITER);
				String retrievedName = columns[0];
				String[] parentTypes = columns[1].split(",");
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
			e.printStackTrace();
		}

		return result;
	}
}
