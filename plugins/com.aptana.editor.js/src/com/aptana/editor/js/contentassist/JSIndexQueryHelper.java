package com.aptana.editor.js.contentassist;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import com.aptana.editor.js.contentassist.index.JSIndexConstants;
import com.aptana.editor.js.contentassist.index.JSIndexReader;
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
			result = this._reader.getTypeProperties(JSIndexQueryHelper.getIndex(), "Window"); //$NON-NLS-1$
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
	 * getIndex
	 * 
	 * @return
	 */
	public static Index getIndex()
	{
		return IndexManager.getInstance().getIndex(URI.create(JSIndexConstants.METADATA));
	}
}
