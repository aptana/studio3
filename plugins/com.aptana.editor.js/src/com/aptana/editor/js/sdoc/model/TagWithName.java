package com.aptana.editor.js.sdoc.model;

public class TagWithName extends Tag
{
	private String _name;

	/**
	 * TagWithName
	 */
	public TagWithName(TagType type, String name, String text)
	{
		super(type, text);

		this._name = name;
	}

	/**
	 * getName
	 * 
	 * @return
	 */
	public String getName()
	{
		return this._name;
	}
}
