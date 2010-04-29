package com.aptana.editor.html.contentassist.index;

import java.io.InputStream;

import com.aptana.index.core.Index;

public class HTMLIndexWriter
{
	private HTMLMetadataReader _reader;
	
	/**
	 * HTMLIndexWriter
	 */
	public HTMLIndexWriter()
	{
		this._reader = new HTMLMetadataReader();
	}
	
	/**
	 * writeToIndex
	 * 
	 * @param index
	 */
	public void writeToIndex(Index index)
	{
	}

	/**
	 * loadXML
	 * 
	 * @param stream
	 * @throws Exception
	 */
	public void loadXML(InputStream stream) throws Exception
	{
		this._reader.loadXML(stream);
	}
}
