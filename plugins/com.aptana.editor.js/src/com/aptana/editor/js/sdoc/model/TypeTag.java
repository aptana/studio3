package com.aptana.editor.js.sdoc.model;

import java.util.List;

public class TypeTag extends TagWithTypes
{
	/**
	 * TypeTag
	 * 
	 * @param types
	 * @param text
	 */
	public TypeTag(List<Type> types, String text)
	{
		super(TagType.TYPE, types, text);
	}
}
