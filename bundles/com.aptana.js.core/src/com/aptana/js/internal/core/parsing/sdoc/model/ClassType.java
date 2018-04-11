/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.parsing.sdoc.model;

import com.aptana.core.util.SourcePrinter;
import com.aptana.js.core.JSTypeConstants;

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
		super(JSTypeConstants.CLASS_TYPE);

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
		writer.print(JSTypeConstants.CLASS_TYPE);
		writer.print('<');
		this._type.toSource(writer);
		writer.print('>');
	}
}
