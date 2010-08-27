package com.aptana.editor.js.sdoc.model;

import java.util.List;

public class PropertyTag extends TagWithTypes
{
	/**
	 * PropertyTag
	 * 
	 * @param types
	 * @param text
	 */
	public PropertyTag(List<Type> types, String text)
	{
		super(TagType.PROPERTY, types, text);
	}
}
