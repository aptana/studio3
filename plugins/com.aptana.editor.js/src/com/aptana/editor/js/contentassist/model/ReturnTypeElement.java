package com.aptana.editor.js.contentassist.model;

import com.aptana.core.util.StringUtil;

public class ReturnTypeElement
{
	private String _description;
	private String _type;

	/**
	 * ReturnTypeElement
	 */
	public ReturnTypeElement()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		boolean result = false;

		if (this == obj)
		{
			result = true;
		}
		else if (obj instanceof ReturnTypeElement)
		{
			ReturnTypeElement that = (ReturnTypeElement) obj;

			result = StringUtil.areEqual(this.getDescription(), that.getDescription()) && StringUtil.areEqual(this.getType(), that.getType());
		}

		return result;
	}

	/**
	 * getDescription
	 * 
	 * @return
	 */
	public String getDescription()
	{
		return this._description;
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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int h = 0;

		if (this._type != null)
		{
			h = this._type.hashCode();
		}
		if (this._description != null)
		{
			h = 31 * h + this._description.hashCode();
		}

		return h;
	}

	/**
	 * setDescription
	 * 
	 * @param description
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
