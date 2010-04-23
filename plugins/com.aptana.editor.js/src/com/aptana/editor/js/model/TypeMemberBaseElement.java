package com.aptana.editor.js.model;

public class TypeMemberBaseElement extends BaseElement
{
	private TypeElement _owningType;

	/**
	 * TypeMemberBaseElement
	 */
	public TypeMemberBaseElement()
	{
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
	 * setOwningType
	 * 
	 * @param type
	 */
	void setOwningType(TypeElement type)
	{
		this._owningType = type;
	}
}
