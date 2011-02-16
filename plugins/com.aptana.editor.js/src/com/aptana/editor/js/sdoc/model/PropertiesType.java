/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.sdoc.model;

import com.aptana.core.util.SourcePrinter;
import com.aptana.editor.js.JSTypeConstants;

public class PropertiesType extends Type
{
	private Type _memberType;

	/**
	 * PropertiesType
	 * 
	 * @param memberType
	 */
	public PropertiesType(Type memberType)
	{
		super(JSTypeConstants.PROPERTIES_TYPE);

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
		writer.print(JSTypeConstants.PROPERTIES_TYPE);
		writer.print(JSTypeConstants.GENERIC_OPEN);
		this._memberType.toSource(writer);
		writer.print(JSTypeConstants.GENERIC_CLOSE);
	}
}
