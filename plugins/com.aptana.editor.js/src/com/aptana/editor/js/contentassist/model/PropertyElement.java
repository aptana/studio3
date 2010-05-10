package com.aptana.editor.js.contentassist.model;

import java.util.LinkedList;
import java.util.List;

public class PropertyElement extends BaseElement
{
	private TypeElement _owningType;
	private boolean _isInstance;
	private boolean _isInvocationOnly;
	private boolean _isInternal;
	private List<ReturnTypeElement> _types = new LinkedList<ReturnTypeElement>();

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
	 * isInstance
	 * 
	 * @return
	 */
	public boolean isInstance()
	{
		return this._isInstance;
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
	 * isInvocationOnly
	 * 
	 * @return
	 */
	public boolean isInvocationOnly()
	{
		return this._isInvocationOnly;
	}

	/**
	 * setIsInstance
	 * 
	 * @param value
	 */
	public void setIsInstance(boolean value)
	{
		this._isInstance = value;
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
	 * setIsInvocationOnly
	 * 
	 * @param value
	 */
	public void setIsInvocationOnly(boolean value)
	{
		this._isInvocationOnly = value;
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
