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
		super(JSTypeConstants.ARRAY_TYPE);

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
		writer.print(JSTypeConstants.ARRAY_TYPE);

		if (this._memberType != Type.OBJECT_TYPE) // $codepro.audit.disable useEquals
		{
			writer.print(JSTypeConstants.GENERIC_OPEN);
			this._memberType.toSource(writer);
			writer.print(JSTypeConstants.GENERIC_CLOSE);
		}
	}
}
