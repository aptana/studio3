package com.aptana.editor.css.contentassist;

import java.io.IOException;
import java.util.List;

import com.aptana.editor.css.contentassist.index.CSSIndexConstants;
import com.aptana.editor.css.contentassist.index.CSSIndexReader;
import com.aptana.editor.css.contentassist.model.ElementElement;
import com.aptana.editor.css.contentassist.model.PropertyElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;

public class CSSIndexQueryHelper
{
	private CSSIndexReader _reader;
	
	/**
	 * CSSContentAssistHelper
	 */
	public CSSIndexQueryHelper()
	{
		this._reader = new CSSIndexReader();
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
		return IndexManager.getInstance().getIndex(CSSIndexConstants.METADATA);
	}

	/**
	 * getProperties
	 * 
	 * @return
	 */
	public List<PropertyElement> getProperties()
	{
		List<PropertyElement> result = null;
		
		try
		{
			result = this._reader.getProperties(this.getIndex());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
		
		return result;
	}
}
