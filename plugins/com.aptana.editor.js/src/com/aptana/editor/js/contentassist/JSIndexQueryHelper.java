package com.aptana.editor.js.contentassist;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aptana.editor.js.contentassist.index.JSIndexConstants;
import com.aptana.editor.js.contentassist.index.JSIndexReader;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.QueryResult;
import com.aptana.index.core.SearchPattern;

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
	 * getProjectGlobals
	 * 
	 * @return
	 */
	public Map<String,String> getProjectGlobals(Index index)
	{
		Map<String,String> result = null;
		
		try
		{
			List<QueryResult> items = index.query(new String[] { JSIndexConstants.FUNCTION }, "*", SearchPattern.PATTERN_MATCH);
			
			if (items != null)
			{
				result = new HashMap<String,String>();
				
				for (QueryResult item : items)
				{
					String[] paths = item.getDocuments();
					String path = (paths != null && paths.length > 0) ? paths[0] : ""; //$NON-NLS-1$
					
					result.put(item.getWord(), path);
				}
			}
		}
		catch (IOException ignore)
		{
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
