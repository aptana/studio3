package com.aptana.editor.js.contentassist;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.aptana.editor.js.contentassist.index.JSIndexConstants;
import com.aptana.editor.js.contentassist.index.JSIndexReader;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;

public class JSIndexQueryHelper
{
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
	 * @return
	 */
	public PropertyElement getCoreGlobal(String name)
	{
		return this.getCoreTypeProperty("Window", name);
	}
	
	/**
	 * getGlobals
	 * 
	 * @return
	 */
	public List<PropertyElement> getCoreGlobals()
	{
		return this.getTypeProperties(this.getIndex(), "Window");
	}
	
	/**
	 * getCoreTypeMethods
	 * 
	 * @param typeName
	 * @return
	 */
	public List<FunctionElement> getCoreTypeMethods(String typeName)
	{
		return this.getTypeMethods(this.getIndex(), typeName);
	}
	
	/**
	 * getCoreTypeProperties
	 * 
	 * @param typeName
	 * @return
	 */
	public List<PropertyElement> getCoreTypeProperties(String typeName)
	{
		return this.getTypeProperties(this.getIndex(), typeName);
	}
	
	/**
	 * getCoreTypeProperty
	 * 
	 * @param typeName
	 * @param propertyName
	 * @return
	 */
	public PropertyElement getCoreTypeProperty(String typeName, String propertyName)
	{
		return this.getTypeProperty(this.getIndex(), typeName, propertyName);
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
	 */
	public List<FunctionElement> getTypeMethods(Index index, String typeName)
	{
		List<FunctionElement> result = null;
		
		try
		{
			result = this._reader.getFunctions(index, typeName);
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
	 */
	public List<PropertyElement> getTypeProperties(Index index, String typeName)
	{
		List<PropertyElement> result = null;
		
		try
		{
			result = this._reader.getProperties(index, typeName);
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
	 * @return
	 */
	public PropertyElement getTypeProperty(Index index, String typeName, String propertyName)
	{
		PropertyElement result = null;
		
		try
		{
			// check in core globals
			result = this._reader.getProperty(index, typeName, propertyName);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return result;
	}
}
