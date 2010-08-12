package com.aptana.editor.js.sdoc.model;

public class UnknownTag extends TagWithName
{
	/**
	 * UnknownTag
	 * 
	 * @param name
	 * @param text
	 */
	public UnknownTag(String name, String text)
	{
		super(TagType.UNKNOWN, name, text);
	}
}
