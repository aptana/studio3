package com.aptana.editor.js.sdoc.model;

import java.util.List;

public class ParamTag extends TagWithTypes
{
	private Parameter _parameter;
	
	/**
	 * ParamTag
	 */
	public ParamTag(Parameter parameter, List<Type> types, String text)
	{
		super(TagType.PARAM, types, text);
		
		this._parameter = parameter;
	}
	
	/**
	 * getName
	 * 
	 * @return
	 */
	public String getName()
	{
		return this._parameter.getName();
	}
}
