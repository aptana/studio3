package com.aptana.editor.js.sdoc.model;

public class ArrayType extends Type
{
	private Type _memberType;
	
	/**
	 * ArrayType
	 */
	public ArrayType()
	{
		this(Type.OBJECT_TYPE);
	}
	
	/**
	 * ArrayType
	 * 
	 * @param memberType
	 */
	public ArrayType(Type memberType)
	{
		super("Array");
		
		this._memberType = (memberType != null) ? memberType : Type.OBJECT_TYPE;
	}
	
	/**
	 * getMemberType
	 * 
	 * @return
	 */
	public Type getMemberType()
	{
		return this._memberType;
	}
}
