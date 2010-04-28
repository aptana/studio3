package com.aptana.editor.css.index;

import java.io.InputStream;

import com.aptana.index.core.Index;

public class CSSIndexWriter
{
	private CSSMetadataReader _reader;
	
	/**
	 * CSSIndexWriter
	 */
	public CSSIndexWriter()
	{
		this._reader = new CSSMetadataReader();
	}
	
	/**
	 * loadXML
	 * 
	 * @param stream
	 * @throws Exception 
	 * @throws ScriptDocException
	 */
	public void loadXML(InputStream stream) throws Exception
	{
		this._reader.loadXML(stream);
	}

	/**
	 * writeToIndex
	 * 
	 * @param index
	 */
	public void writeToIndex(Index index)
	{
	}
}
