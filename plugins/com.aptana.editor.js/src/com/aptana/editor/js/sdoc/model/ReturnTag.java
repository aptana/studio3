package com.aptana.editor.js.sdoc.model;

import java.util.List;

public class ReturnTag extends TagWithTypes
{
	/**
	 * ReturnTag
	 * 
	 * @param types
	 * @param text
	 */
	public ReturnTag(List<Type> types, String text)
	{
		super(TagType.RETURN, types, text);
	}
}
