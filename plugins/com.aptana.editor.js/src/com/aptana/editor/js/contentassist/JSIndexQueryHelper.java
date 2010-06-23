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
	 * getGlobals
	 * 
	 * @return
	 */
	public List<PropertyElement> getCoreGlobals()
	{
		List<PropertyElement> result = null;
		
		try
		{
			result = this._reader.getTypeProperties(this.getIndex(), "Window"); //$NON-NLS-1$
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return result;
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
		
		try
		{
			// check in core globals
			result = this._reader.getProperty(this.getIndex(), "Window", name);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
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
			result = this._reader.getFunctions(this.getIndex(), typeName);
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
			result = this._reader.getProperties(this.getIndex(), typeName);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return result;
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
}
