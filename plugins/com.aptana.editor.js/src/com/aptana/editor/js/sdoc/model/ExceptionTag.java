package com.aptana.editor.js.sdoc.model;

import java.util.List;

public class ExceptionTag extends TagWithTypes
{
	/**
	 * ExceptionTag
	 * 
	 * @param types
	 * @param text
	 */
	public ExceptionTag(List<Type> types, String text)
	{
		super(TagType.EXCEPTION, types, text);
	}
}
