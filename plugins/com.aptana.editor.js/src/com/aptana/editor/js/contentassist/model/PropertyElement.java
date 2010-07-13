package com.aptana.editor.js.contentassist.model;

import java.util.ArrayList;
import java.util.List;

public class PropertyElement extends BaseElement
{
	private TypeElement _owningType;
	private boolean _isInstanceProperty;
	private boolean _isClassProperty;
	private boolean _isInternal;
	private List<ReturnTypeElement> _types = new ArrayList<ReturnTypeElement>();

	/**
	 * PropertyElement
	 */
	public PropertyElement()
	{
	}

	/**
	 * addType
	 * 
	 * @param type
	 */
	public void addType(ReturnTypeElement type)
	{
		this._types.add(type);
	}

	/**
	 * getOwningType
	 * 
	 * @return
	 */
	public TypeElement getOwningType()
	{
		return this._owningType;
	}

	/**
	 * getTypeNames
	 * 
	 * @return
	 */
	public String[] getTypeNames()
	{
		String[] result = new String[this._types.size()];

		for (int i = 0; i < result.length; i++)
		{
			result[i] = this._types.get(i).getType();
		}

		return result;
	}

	/**
	 * getTypes
	 * 
	 * @return
	 */
	public ReturnTypeElement[] getTypes()
	{
		return this._types.toArray(new ReturnTypeElement[this._types.size()]);
	}

	/**
	 * isInstanceProperty
	 * 
	 * @return
	 */
	public boolean isInstanceProperty()
	{
		return this._isInstanceProperty;
	}

	/**
	 * isInternal
	 * 
	 * @return
	 */
	public boolean isInternal()
	{
		return this._isInternal;
	}

	/**
	 * isClassProperty
	 * 
	 * @return
	 */
	public boolean isClassProperty()
	{
		return this._isClassProperty;
	}

	/**
	 * setIsInstanceProperty
	 * 
	 * @param value
	 */
	public void setIsInstanceProperty(boolean value)
	{
		this._isInstanceProperty = value;
	}

	/**
	 * setIsInternal
	 * 
	 * @param value
	 */
	public void setIsInternal(boolean value)
	{
		this._isInternal = value;
	}

	/**
	 * setIsClassProperty
	 * 
	 * @param value
	 */
	public void setIsClassProperty(boolean value)
	{
		this._isClassProperty = value;
	}

	/**
	 * setOwningType
	 * 
	 * @param type
	 */
	void setOwningType(TypeElement type)
	{
		this._owningType = type;
	}
}
