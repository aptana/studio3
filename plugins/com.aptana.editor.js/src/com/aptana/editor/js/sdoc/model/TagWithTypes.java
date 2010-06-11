package com.aptana.editor.js.sdoc.model;

import java.util.List;

public class TagWithTypes extends Tag
{
	private List<Type> _types;
	
	/**
	 * ExceptionTag
	 */
	public TagWithTypes(TagType type, List<Type> types, String text)
	{
		super(type, text);
		
		this._types = types;
	}
	
	/**
	 * getTypes
	 * 
	 * @return
	 */
	public List<Type> getTypes()
	{
		return this._types;
	}
}
