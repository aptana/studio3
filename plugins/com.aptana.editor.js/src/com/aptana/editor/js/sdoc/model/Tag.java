package com.aptana.editor.js.sdoc.model;

import beaver.Symbol;

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
}
