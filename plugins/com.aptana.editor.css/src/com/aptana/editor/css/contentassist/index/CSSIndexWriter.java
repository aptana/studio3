package com.aptana.editor.css.contentassist.index;

import java.io.InputStream;

import com.aptana.editor.css.contentassist.model.ElementElement;
import com.aptana.editor.css.contentassist.model.PropertyElement;
import com.aptana.index.core.Index;
import com.aptana.util.StringUtil;

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
	 * getDocumentPath
	 * 
	 * @return
	 */
	protected String getDocumentPath()
	{
		return CSSIndexConstants.METADATA;
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
	 * writeElement
	 * 
	 * @param index
	 * @param element
	 */
	protected void writeElement(Index index, ElementElement element)
	{
		String[] columns = new String[] {
			element.getName(),
			element.getDisplayName(),
			element.getDescription(),
			element.getExample(),
			element.getRemark()
			// properties
			// user agents
		};
		String key = StringUtil.join(CSSIndexConstants.DELIMITER, columns);
		
		index.addEntry(CSSIndexConstants.ELEMENT, key, this.getDocumentPath());
	}
	
	/**
	 * writeProperty
	 * 
	 * @param index
	 * @param property
	 */
	protected void writeProperty(Index index, PropertyElement property)
	{
		
	}
	
	/**
	 * writeToIndex
	 * 
	 * @param index
	 */
	public void writeToIndex(Index index)
	{
		for (ElementElement element : this._reader.getElements())
		{
			this.writeElement(index, element);
		}
		
		for (PropertyElement property : this._reader.getProperties())
		{
			this.writeProperty(index, property);
		}
	}
}
