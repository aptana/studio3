package com.aptana.editor.js.sdoc.model;

public class NamespaceTag extends TagWithName
{
	/**
	 * NamespaceTag
	 * 
	 * @param name
	 * @param text
	 */
	public NamespaceTag(String name, String text)
	{
		super(TagType.NAMESPACE, name, text);
	}
}
