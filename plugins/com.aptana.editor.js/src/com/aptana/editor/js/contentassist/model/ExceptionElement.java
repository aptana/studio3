package com.aptana.editor.js.contentassist.model;

public class ExceptionElement
{
	private String _type;
	private String _description;

	/**
	 * ExceptionElement
	 */
	public ExceptionElement()
	{
	}

	/**
	 * getDescription
	 */
	public String getDescription()
	{
		return this._description;
	}

	/**
	 * getType
	 */
	public String getType()
	{
		return this._type;
	}

	/**
	 * setDescription
	 */
	public void setDescription(String description)
	{
		this._description = description;
	}

	/**
	 * setType
	 * 
	 * @param type
	 */
	public void setType(String type)
	{
		this._type = type;
	}
}
