package com.aptana.editor.js.sdoc.model;

public class ClassDescriptionTag extends TagWithName
{
	/**
	 * ClassDescription
	 * 
	 * @param name
	 * @param text
	 */
	public ClassDescriptionTag(String name, String text)
	{
		super(TagType.CLASS_DESCRIPTION, name, text);
	}
}
