package com.aptana.editor.js.contentassist;

import java.io.IOException;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.aptana.editor.js.contentassist.index.JSIndexConstants;
import com.aptana.editor.js.contentassist.index.JSIndexReader;
import com.aptana.editor.js.contentassist.model.FieldSelector;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;

public class JSIndexQueryHelper
{
	private static final EnumSet<FieldSelector> PARENT_TYPES = EnumSet.of(FieldSelector.PARENT_TYPES);
	
	private JSIndexReader _reader;
	
	/**
	 * JSContentAssistant
	 */
	public JSIndexQueryHelper()
	{
		this._reader = new JSIndexReader();
	}
	
	/**
	 * getCoreGlobal
	 * 
	 * @param name
	 * @param fields
	 * @return
	 */
	public PropertyElement getCoreGlobal(String name, EnumSet<FieldSelector> fields)
	{
		return this.getCoreTypeProperty("Window", name, fields);
	}
	
	/**
	 * getGlobals
	 * 
	 * @param fields
	 * @return
	 */
	public List<PropertyElement> getCoreGlobals(EnumSet<FieldSelector> fields)
	{
		return this.getTypeProperties(this.getIndex(), "Window", fields);
	}
	
	/**
	 * getCoreTypeMethods
	 * 
	 * @param typeName
	 * @param fields
	 * @return
	 */
	public List<FunctionElement> getCoreTypeMethods(String typeName, EnumSet<FieldSelector> fields)
	{
		return this.getTypeMethods(this.getIndex(), typeName, fields);
	}
	
	/**
	 * getCoreTypeProperties
	 * 
	 * @param typeName
	 * @param fields
	 * @return
	 */
	public List<PropertyElement> getCoreTypeProperties(String typeName, EnumSet<FieldSelector> fields)
	{
		return this.getTypeProperties(this.getIndex(), typeName, fields);
	}
	
	/**
	 * getCoreTypeProperty
	 * 
	 * @param typeName
	 * @param propertyName
	 * @param fields
	 * @return
	 */
	public PropertyElement getCoreTypeProperty(String typeName, String propertyName, EnumSet<FieldSelector> fields)
	{
		return this.getTypeProperty(this.getIndex(), typeName, propertyName, fields);
	}
	
	/**
	 * getIndex
	 * 
	 * @return
	 */
	private Index getIndex()
	{
		return IndexManager.getInstance().getIndex(JSIndexConstants.METADATA);
	}
	
	/**
	 * getProjectGlobal
	 * 
	 * @param index
	 * @param name
	 * @return
	 */
	public PropertyElement getProjectGlobal(Index index, String name)
	{
		PropertyElement result = null;
		
//		try
//		{
//			result = this._reader.getProperty(index, name);
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}
		
		return result;
	}
	
	/**
	 * getProjectGlobals
	 * 
	 * @return
	 */
	public Map<String,List<String>> getProjectGlobals(Index index)
	{
		return this._reader.getValues(index, JSIndexConstants.FUNCTION);
	}
	
	/**
	 * getProjectVariables
	 * 
	 * @return
	 */
	public Map<String,List<String>> getProjectVariables(Index index)
	{
		return this._reader.getValues(index, JSIndexConstants.VARIABLE);
	}
	
	/**
	 * getTypeMethods
	 * 
	 * @param index
	 * @param typeName
	 * @param fields
	 * @return
	 */
	public List<FunctionElement> getTypeMethods(Index index, String typeName, EnumSet<FieldSelector> fields)
	{
		List<FunctionElement> result = null;
		
		try
		{
			result = this._reader.getFunctions(index, typeName, fields);
		}
		catch (IOException e)
		{
			e.printStackTrace();
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
	 */
	public List<PropertyElement> getTypeProperties(Index index, String typeName, EnumSet<FieldSelector> fields)
	{
		List<PropertyElement> result = null;
		
		try
		{
			result = this._reader.getProperties(index, typeName, fields);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * getTypeProperty
	 * 
	 * @param index
	 * @param typeName
	 * @param propertyName
	 * @param fields
	 * @return
	 */
	public PropertyElement getTypeProperty(Index index, String typeName, String propertyName, EnumSet<FieldSelector> fields)
	{
		PropertyElement result = null;
		LinkedList<String> types = new LinkedList<String>();
		
		types.add(typeName);
		
		try
		{
			while (types.size() > 0)
			{
				String currentType = types.remove();
				
				// check in core globals
				result = this._reader.getProperty(index, currentType, propertyName, fields);
				
				if (result != null)
				{
					break;
				}
				else
				{
					TypeElement type = this._reader.getType(index, currentType, PARENT_TYPES);
					
					for (String superclass : type.getParentTypes())
					{
						types.add(superclass);
					}
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
