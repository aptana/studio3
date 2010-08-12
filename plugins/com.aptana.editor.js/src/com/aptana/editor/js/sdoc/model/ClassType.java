package com.aptana.editor.js.sdoc.model;

import com.aptana.editor.js.JSTypeConstants;
import com.aptana.parsing.io.SourcePrinter;

public class ClassType extends Type
{
	private Type _type;

	/**
	 * ClassType
	 */
	public ClassType()
	{
		this(Type.OBJECT_TYPE);
	}

	/**
	 * ArrayType
	 * 
	 * @param type
	 */
	public ClassType(Type type)
	{
		super(JSTypeConstants.CLASS_TYPE); //$NON-NLS-1$

		this._type = (type != null) ? type : Type.OBJECT_TYPE;
	}

	/**
	 * getType
	 * 
	 * @return
	 */
	public Type getType()
	{
		return this._type;
	}

	/**
	 * toSource
	 * 
	 * @param writer
	 */
	public void toSource(SourcePrinter writer)
	{
		writer.print(JSTypeConstants.CLASS_TYPE); //$NON-NLS-1$
		writer.print("<"); //$NON-NLS-1$
		this._type.toSource(writer);
		writer.print(">"); //$NON-NLS-1$
	}
}
