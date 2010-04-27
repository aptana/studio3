package com.aptana.editor.js.index;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import com.aptana.editor.js.model.FunctionElement;
import com.aptana.editor.js.model.ParameterElement;
import com.aptana.editor.js.model.PropertyElement;
import com.aptana.editor.js.model.TypeElement;
import com.aptana.index.core.Index;
import com.aptana.util.StringUtil;

public class JSIndexWriter
{
	private JSMetadataReader _reader;
	private int _descriptionCount;
	private int _parameterCount;

	/**
	 * JSMetadataIndexer
	 */
	public JSIndexWriter()
	{
		this._reader = new JSMetadataReader();
	}

	/**
	 * getDocumentPath
	 * 
	 * @return
	 */
	protected String getDocumentPath()
	{
		return "";
	}
	
	/**
	 * loadXML
	 * 
	 * @param stream
	 * @throws ScriptDocException
	 */
	public void loadXML(InputStream stream) throws ScriptDocException
	{
		this._reader.loadXML(stream);
	}

	/**
	 * writeDescription
	 * 
	 * @param description
	 */
	protected String writeDescription(Index index, String description)
	{
		String indexString = Integer.toString(this._descriptionCount++);
		String value = indexString + IndexConstants.DELIMITER + description;
		
		index.addEntry(IndexConstants.DESCRIPTION, value, this.getDocumentPath());
		
		return indexString;
	}
	
	/**
	 * writeFunction
	 * 
	 * @param index
	 * @param type
	 * @param function
	 */
	protected void writeFunction(Index index, FunctionElement function)
	{
		String parametersKey = this.writeParameters(index, function.getParameters());
		// ReturnTypeElement[] returnTypes = function.getReturnTypes();
		String descriptionKey = this.writeDescription(index, function.getDescription());
		// SinceElement[] sinceList = function.getSinceList();
		// UserAgentElement[] userAgents = function.getUserAgents();
		
		String value = StringUtil.join(
			IndexConstants.DELIMITER,
			function.getName(),
			function.getOwningType().getName(),
			descriptionKey,
			parametersKey
		);

		index.addEntry(IndexConstants.FUNCTION, value, this.getDocumentPath());
	}

	/**
	 * writeParameters
	 * 
	 * @param index
	 * @param parameters
	 * @return
	 */
	protected String writeParameters(Index index, ParameterElement[] parameters)
	{
		List<String> keyList = new LinkedList<String>();
		String indexString = Integer.toString(this._parameterCount++);
		
		keyList.add(indexString);
		
		for (int i = 0; i < parameters.length; i++)
		{
			ParameterElement parameter = parameters[i];
			String name = parameter.getName();
			String usage = parameter.getUsage();
			String types = StringUtil.join(",", parameter.getTypes());
			
			keyList.add(name + "," + usage + "," + types);
		}
		
		String value = StringUtil.join(IndexConstants.DELIMITER, keyList);
		
		index.addEntry(IndexConstants.PARAMETERS, value, this.getDocumentPath());
		
		return indexString;
	}
	
	/**
	 * writeProperty
	 * 
	 * @param index
	 * @param type
	 * @param property
	 */
	protected void writeProperty(Index index, PropertyElement property)
	{
		String propertyTypes = StringUtil.join(",", property.getTypeNames());
		String descriptionKey = this.writeDescription(index, property.getDescription());
		// SinceElement[] sinceList = property.getSinceList();
		// UserAgentElement[] userAgents = property.getUserAgents();
		
		String value = StringUtil.join(
			IndexConstants.DELIMITER,
			property.getName(),
			property.getOwningType().getName(),
			descriptionKey,
			propertyTypes
		);

		index.addEntry(IndexConstants.PROPERTY, value, this.getDocumentPath());
	}

	/**
	 * writeToIndex
	 * 
	 * @param index
	 */
	public void writeToIndex(Index index)
	{
		TypeElement[] types = this._reader.getTypes();

		for (TypeElement type : types)
		{
			this.writeType(index, type);
		}
	}

	/**
	 * writeType
	 * 
	 * @param index
	 * @param type
	 */
	public void writeType(Index index, TypeElement type)
	{
		String documentPath = "";

		// write type entry
		String[] parentTypes = type.getParentTypes();
		String descriptionKey = this.writeDescription(index, type.getDescription());
		// SinceElement[] sinceList = type.getSinceList();
		// UserAgentElement[] userAgents = type.getUserAgents();

		// calculate key value and add to index
		String value = StringUtil.join(
			IndexConstants.DELIMITER,
			type.getName(),
			(parentTypes.length > 0) ? StringUtil.join(",", parentTypes) : "Object",
			descriptionKey
		);

		index.addEntry(IndexConstants.TYPE, value, documentPath);

		// write type properties (that are not functions)
		for (PropertyElement property : type.getProperties())
		{
			if (property instanceof FunctionElement)
			{
				this.writeFunction(index, (FunctionElement) property);
			}
			else
			{
				this.writeProperty(index, property);
			}
		}
	}
}
