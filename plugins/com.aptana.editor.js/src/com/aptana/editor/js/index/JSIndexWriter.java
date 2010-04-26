package com.aptana.editor.js.index;

import java.io.InputStream;

import com.aptana.editor.js.model.FunctionElement;
import com.aptana.editor.js.model.PropertyElement;
import com.aptana.editor.js.model.TypeElement;
import com.aptana.index.core.Index;
import com.aptana.util.StringUtil;

public class JSIndexWriter
{
	private JSMetadataReader _reader;
	private int _descriptionCount;

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
		String indexString = Integer.toString(this._descriptionCount);
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
		// ParameterElement[] parameters = function.getParameters();
		// ReturnTypeElement[] returnTypes = function.getReturnTypes();
		String descriptionKey = this.writeDescription(index, function.getDescription());
		// SinceElement[] sinceList = function.getSinceList();
		// UserAgentElement[] userAgents = function.getUserAgents();
		
		String value = StringUtil.join(
			IndexConstants.DELIMITER,
			function.getName(),
			function.getOwningType().getName(),
			descriptionKey
		);

		index.addEntry(IndexConstants.FUNCTION, value, this.getDocumentPath());
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
		// String description = property.getDescription();
		// SinceElement[] sinceList = property.getSinceList();
		// UserAgentElement[] userAgents = property.getUserAgents();
		
		String value = StringUtil.join(
			IndexConstants.DELIMITER,
			property.getName(),
			property.getOwningType().getName(),
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
