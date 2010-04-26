package com.aptana.editor.js.index;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import com.aptana.editor.js.model.FunctionElement;
import com.aptana.editor.js.model.PropertyElement;
import com.aptana.editor.js.model.TypeElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.QueryResult;
import com.aptana.index.core.SearchPattern;

public class JSIndexReader
{
	public String getDescription(Index index, String descriptionKey) throws IOException
	{
		String result;
		
		// grab description
		String descriptionPattern = descriptionKey + IndexConstants.DELIMITER;
		List<QueryResult> descriptions = index.query(new String[] { IndexConstants.DESCRIPTION }, descriptionPattern, SearchPattern.PREFIX_MATCH);
		
		if (descriptions != null)
		{
			String descriptionValue = descriptions.get(0).getWord();
			
			result = descriptionValue.substring(descriptionValue.indexOf(IndexConstants.DELIMITER) + 1);
		}
		else
		{
			result = "";
		}
		
		return result;
	}
	
	/**
	 * readType
	 * 
	 * @param index
	 * @param typeName
	 * @return
	 */
	public TypeElement readType(Index index, String typeName)
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
				
				result = new TypeElement();
				result.setName(retrievedName);
				for (String parentType : parentTypes)
				{
					result.addParentType(parentType);
				}
				result.setDescription(this.getDescription(index, descriptionKey));

				// create pattern for doing member queries
				String memberPattern = MessageFormat.format(
					"^[^{0}]+{0}{1}(?:{0}|$)",
					new Object[] { IndexConstants.DELIMITER, typeName }
				);
				
				// read properties
				List<QueryResult> properties = index.query(new String[] { IndexConstants.PROPERTY }, memberPattern, SearchPattern.REGEX_MATCH);
				
				if (properties != null)
				{
					for (QueryResult property : properties)
					{
						String[] propertyColumns = property.getWord().split(IndexConstants.DELIMITER);
						String propertyName = propertyColumns[0];
						
						PropertyElement p = new PropertyElement();
						p.setName(propertyName);
						
						result.addProperty(p);
					}
				}
				
				// read methods
				List<QueryResult> methods = index.query(new String[] { IndexConstants.FUNCTION }, memberPattern, SearchPattern.REGEX_MATCH);
				
				if (methods != null)
				{
					for (QueryResult method : methods)
					{
						String[] methodColumns = method.getWord().split(IndexConstants.DELIMITER);
						String methodName = methodColumns[0];
						
						descriptionKey = methodColumns[2]; 
						
						FunctionElement f = new FunctionElement();
						f.setName(methodName);
						f.setDescription(this.getDescription(index, descriptionKey));
						
						result.addProperty(f);
					}
				}
				
				// read description
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * getFunction
	 * 
	 * @param index
	 * @param owningType
	 * @param name
	 * @return
	 */
	public FunctionElement getFunction(Index index, String owningType, String name)
	{
		String pattern = MessageFormat.format("{1}{0}{2}", IndexConstants.DELIMITER, name, owningType);
		FunctionElement result = null;
		
		try
		{
			List<QueryResult> functions = index.query(new String[] { IndexConstants.FUNCTION }, pattern, SearchPattern.PREFIX_MATCH);
			
			if (functions != null)
			{
				for (QueryResult function : functions)
				{
					String[] methodColumns = function.getWord().split(IndexConstants.DELIMITER);
					String methodName = methodColumns[0];
					String descriptionKey = methodColumns[2]; 
					
					result = new FunctionElement();
					result.setName(methodName);
					result.setDescription(this.getDescription(index, descriptionKey));
					
					// TODO: only handling the first function right now
					break;
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
