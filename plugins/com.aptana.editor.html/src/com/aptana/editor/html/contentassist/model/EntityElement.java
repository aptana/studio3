package com.aptana.editor.html.contentassist.model;

public class EntityElement
{
	private String _name;
	private String _decimalValue;
	private String _hexValue;
	private String _description;

	public EntityElement()
	{
	}

	/**
	 * getDecimalValue
	 * 
	 * @return the decimalValue
	 */
	public String getDecimalValue()
	{
		return this._decimalValue;
	}

	/**
	 * getDescription
	 * 
	 * @return the description
	 */
	public String getDescription()
	{
		return this._description;
	}

	/**
	 * getHexValue
	 * 
	 * @return the hexValue
	 */
	public String getHexValue()
	{
		return this._hexValue;
	}

	/**
	 * getName
	 * 
	 * @return the name
	 */
	public String getName()
	{
		return this._name;
	}

	/**
	 * setDecimalValue
	 * 
	 * @param decimalValue
	 *            the value to set
	 */
	public void setDecimalValue(String value)
	{
		this._decimalValue = value;
	}

	/**
	 * setDescription
	 * 
	 * @param description the description to set
	 */
	public void setDescription(String description)
	{
		this._description = description;
	}

	/**
	 * setHexValue
	 * 
	 * @param hexValue
	 *            the hexValue to set
	 */
	public void setHexValue(String value)
	{
		this._hexValue = value;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name)
	{
		this._name = name;
	}
}
