package com.aptana.editor.js.sdoc.model;

import com.aptana.editor.js.JSTypeConstants;
import com.aptana.parsing.io.SourcePrinter;

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
		super(JSTypeConstants.ARRAY_TYPE); //$NON-NLS-1$

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
	public void toSource(SourcePrinter writer)
	{
		writer.print(JSTypeConstants.ARRAY_TYPE); //$NON-NLS-1$

		if (this._memberType != Type.OBJECT_TYPE)
		{
			writer.print("<"); //$NON-NLS-1$
			this._memberType.toSource(writer);
			writer.print(">"); //$NON-NLS-1$
		}
	}
}
