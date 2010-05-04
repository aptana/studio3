package com.aptana.editor.html.contentassist;

import java.io.IOException;
import java.util.List;

import com.aptana.editor.html.contentassist.index.HTMLIndexConstants;
import com.aptana.editor.html.contentassist.index.HTMLIndexReader;
import com.aptana.editor.html.contentassist.model.ElementElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;

public class HTMLIndexQueryHelper
{
	private HTMLIndexReader _reader;
	
	/**
	 * HTMLContentAssistHelper
	 */
	public HTMLIndexQueryHelper()
	{
		this._reader = new HTMLIndexReader();
	}
	
	/**
	 * getElements
	 * 
	 * @return
	 */
	public List<ElementElement> getElements()
	{
		List<ElementElement> result = null;
		
		try
		{
			result = this._reader.getElements(this.getIndex());
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
		return IndexManager.getInstance().getIndex(HTMLIndexConstants.METADATA);
	}
}
