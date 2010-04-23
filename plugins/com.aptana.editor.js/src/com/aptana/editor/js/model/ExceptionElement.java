package com.aptana.editor.js.model;

public class ExceptionElement extends BaseElement
{
	/**
	 * ExceptionElement
	 */
	public ExceptionElement()
	{
	}

	/**
	 * getType
	 */
	public String getType()
	{
		return this.getName();
	}

	/**
	 * setType
	 * 
	 * @param type
	 */
	public void setType(String type)
	{
		this.setName(type);
	}
}
