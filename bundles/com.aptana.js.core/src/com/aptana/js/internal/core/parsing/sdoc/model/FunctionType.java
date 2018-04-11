/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.parsing.sdoc.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.aptana.core.util.SourcePrinter;
import com.aptana.js.core.JSTypeConstants;

public class FunctionType extends Type
{
	private List<Type> _parameterTypes;
	private List<Type> _returnTypes;

	/**
	 * FunctionType
	 */
	public FunctionType()
	{
		super(JSTypeConstants.FUNCTION_TYPE);
	}

	/**
	 * addParameterType
	 * 
	 * @param parameterType
	 */
	public void addParameterType(Type parameterType)
	{
		if (parameterType != null)
		{
			if (this._parameterTypes == null)
			{
				this._parameterTypes = new ArrayList<Type>();
			}

			this._parameterTypes.add(parameterType);
		}
	}

	/**
	 * addReturnType
	 * 
	 * @param parameterType
	 */
	public void addReturnType(Type returnType)
	{
		if (returnType != null)
		{
			if (this._returnTypes == null)
			{
				this._returnTypes = new ArrayList<Type>();
			}

			this._returnTypes.add(returnType);
		}
	}

	/**
	 * getReturnTypes
	 * 
	 * @return
	 */
	public List<Type> getReturnTypes()
	{
		List<Type> result = this._returnTypes;

		if (result == null)
		{
			result = Collections.emptyList();
		}

		return result;
	}

	/**
	 * toSource
	 * 
	 * @param writer
	 */
	public void toSource(SourcePrinter writer)
	{
		writer.print(JSTypeConstants.FUNCTION_TYPE);

		boolean first;

		if (this._parameterTypes != null && !this._parameterTypes.isEmpty())
		{
			first = true;

			writer.print('(');

			for (Type type : this._parameterTypes)
			{
				if (!first)
				{
					writer.print(',');
				}
				else
				{
					first = false;
				}

				type.toSource(writer);
			}

			writer.print(')');
		}

		if (this._returnTypes != null && !this._returnTypes.isEmpty())
		{
			first = true;

			writer.print("->"); //$NON-NLS-1$

			if (this._returnTypes.size() > 1)
			{
				writer.print('(');
			}

			for (Type type : this._returnTypes)
			{
				if (!first)
				{
					writer.print(',');
				}
				else
				{
					first = false;
				}

				type.toSource(writer);
			}

			if (this._returnTypes.size() > 1)
			{
				writer.print(')');
			}
		}
	}
}
