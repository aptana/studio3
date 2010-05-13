package com.aptana.editor.js.contentassist.model;

public class AliasElement
{
	private String _name;
	private String _type;

	/**
	 * AliasElement
	 */
	public AliasElement()
	{
	}

	/**
	 * getName
	 * 
	 * @return
	 */
	public String getName()
	{
		return this._name;
	}

	/**
	 * getType
	 * 
	 * @return
	 */
	public String getType()
	{
		return this._type;
	}

	/**
	 * setName
	 * 
	 * @param name
	 */
	public void setName(String name)
	{
		this._name = name;
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
