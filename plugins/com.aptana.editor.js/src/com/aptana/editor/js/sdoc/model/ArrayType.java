package com.aptana.editor.js.sdoc.model;

import com.aptana.parsing.io.SourceWriter;

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
	
	/**
	 * toSource
	 * 
	 * @param writer
	 */
	public void toSource(SourceWriter writer)
	{
		writer.print("Array");
		
		if (this._memberType != Type.OBJECT_TYPE)
		{
			writer.print("<");
			this._memberType.toSource(writer);
			writer.print(">");
		}
	}
}
