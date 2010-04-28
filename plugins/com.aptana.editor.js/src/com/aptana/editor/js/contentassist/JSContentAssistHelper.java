package com.aptana.editor.js.contentassist;

import java.io.IOException;
import java.util.List;

import com.aptana.editor.js.index.IndexConstants;
import com.aptana.editor.js.index.JSIndexReader;
import com.aptana.editor.js.model.PropertyElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;

public class JSContentAssistHelper
{
	private JSIndexReader _reader;
	
	/**
	 * JSContentAssistant
	 */
	public JSContentAssistHelper()
	{
		this._reader = new JSIndexReader();
	}
	
	/**
	 * getGlobals
	 * 
	 * @return
	 */
	public List<PropertyElement> getGlobals()
	{
		List<PropertyElement> result = null;
		
		try
		{
			result = this._reader.getTypeProperties(this.getIndex(), "Window");
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
		return IndexManager.getInstance().getIndex(IndexConstants.METADATA);
	}
}
