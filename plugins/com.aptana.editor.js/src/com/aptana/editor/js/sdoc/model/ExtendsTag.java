package com.aptana.editor.js.sdoc.model;

import java.util.List;

public class ExtendsTag extends TagWithTypes
{
	/**
	 * ExtendsTag
	 * 
	 * @param types
	 * @param text
	 */
	public ExtendsTag(List<Type> types, String text)
	{
		super(TagType.EXTENDS, types, text);
	}
}
