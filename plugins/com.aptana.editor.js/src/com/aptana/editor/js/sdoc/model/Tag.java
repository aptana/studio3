package com.aptana.editor.js.sdoc.model;

import beaver.Symbol;

import com.aptana.parsing.io.SourceWriter;

public class Tag extends Symbol
{
	private TagType _type;
	private String _text;

	/**
	 * Tag
	 * 
	 * @param name
	 */
	public Tag(TagType type)
	{
		this(type, "");
	}

	/**
	 * Tag
	 * 
	 * @param name
	 */
	public Tag(TagType type, String text)
	{
		this._type = type;
		this._text = text;
	}

	/**
	 * getText
	 * 
	 * @return
	 */
	public String getText()
	{
		return this._text;
	}

	/**
	 * getType
	 * 
	 * @return
	 */
	public TagType getType()
	{
		return this._type;
	}
	
	/**
	 * toSource
	 * 
	 * @return
	 */
	public String toSource()
	{
		SourceWriter writer = new SourceWriter();
		
		this.toSource(writer);
		
		writer.println();
		
		return writer.toString();
	}
	
	/**
	 * toSource
	 * 
	 * @param writer
	 */
	public void toSource(SourceWriter writer)
	{
		writer.print(this._type.toString());
		
		if (this._text != null && this._text.isEmpty() == false)
		{
			writer.print(" ").print(this._text);
		}
	}
}
