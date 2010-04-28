package com.aptana.editor.js.contentassist.index;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.ParameterElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.ReturnTypeElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.index.core.Index;
import com.aptana.util.StringUtil;

public class JSIndexWriter
{
	private JSMetadataReader _reader;
	private int _descriptionCount;
	private int _parameterCount;
	private int _returnTypeCount;

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
		String indexString;
		
		if (description != null && description.length() > 0)
		{
			indexString = Integer.toString(this._descriptionCount++);
			
			String value = indexString + JSIndexConstants.DELIMITER + description;
			
			index.addEntry(JSIndexConstants.DESCRIPTION, value, this.getDocumentPath());
		}
		else
		{
			indexString = JSIndexConstants.NO_ENTRY;
		}
		
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
		String returnTypesKey = this.writeReturnTypes(index, function.getReturnTypes());
		String descriptionKey = this.writeDescription(index, function.getDescription());
		// SinceElement[] sinceList = function.getSinceList();
		// UserAgentElement[] userAgents = function.getUserAgents();
		
		String value = StringUtil.join(
			JSIndexConstants.DELIMITER,
			function.getName(),
			function.getOwningType().getName(),
			descriptionKey,
			parametersKey,
			returnTypesKey
		);

		index.addEntry(JSIndexConstants.FUNCTION, value, this.getDocumentPath());
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
		
		String value = StringUtil.join(JSIndexConstants.DELIMITER, keyList);
		
		index.addEntry(JSIndexConstants.PARAMETERS, value, this.getDocumentPath());
		
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
		String propertyTypesKey = this.writeReturnTypes(index, property.getTypes());
		String descriptionKey = this.writeDescription(index, property.getDescription());
		// SinceElement[] sinceList = property.getSinceList();
		// UserAgentElement[] userAgents = property.getUserAgents();
		
		String value = StringUtil.join(
			JSIndexConstants.DELIMITER,
			property.getName(),
			property.getOwningType().getName(),
			descriptionKey,
			propertyTypesKey
		);

		index.addEntry(JSIndexConstants.PROPERTY, value, this.getDocumentPath());
	}

	/**
	 * writeReturnTypes
	 * 
	 * @param index
	 * @param returnTypes
	 * @return
	 */
	protected String writeReturnTypes(Index index, ReturnTypeElement[] returnTypes)
	{
		List<String> keyList = new LinkedList<String>();
		String indexString = Integer.toString(this._returnTypeCount++);
		
		keyList.add(indexString);
		
		for (ReturnTypeElement returnType : returnTypes)
		{
			String type = returnType.getType();
			String descriptionKey = this.writeDescription(index, returnType.getDescription());
			
			keyList.add(type + "," + descriptionKey);
		}
		
		String value = StringUtil.join(JSIndexConstants.DELIMITER, keyList);
		
		index.addEntry(JSIndexConstants.RETURN_TYPES, value, this.getDocumentPath());
		
		return indexString;
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
			JSIndexConstants.DELIMITER,
			type.getName(),
			(parentTypes.length > 0) ? StringUtil.join(",", parentTypes) : "Object",
			descriptionKey
		);

		index.addEntry(JSIndexConstants.TYPE, value, documentPath);

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
