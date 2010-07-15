package com.aptana.editor.js.sdoc.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.aptana.editor.js.JSTypeConstants;
import com.aptana.parsing.io.SourcePrinter;

public class FunctionType extends Type
{
	private List<Type> _parameterTypes;
	private List<Type> _returnTypes;

	/**
	 * FunctionType
	 */
	public FunctionType()
	{
		super(JSTypeConstants.FUNCTION); //$NON-NLS-1$
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
		writer.print(JSTypeConstants.FUNCTION); //$NON-NLS-1$

		boolean first;

		if (this._parameterTypes != null && this._parameterTypes.isEmpty() == false)
		{
			first = true;

			writer.print("("); //$NON-NLS-1$

			for (Type type : this._parameterTypes)
			{
				if (first == false)
				{
					writer.print(","); //$NON-NLS-1$
				}
				else
				{
					first = false;
				}

				type.toSource(writer);
			}

			writer.print(")"); //$NON-NLS-1$
		}

		if (this._returnTypes != null && this._returnTypes.isEmpty() == false)
		{
			first = true;

			writer.print("->"); //$NON-NLS-1$

			if (this._returnTypes.size() > 1)
			{
				writer.print("("); //$NON-NLS-1$
			}

			for (Type type : this._returnTypes)
			{
				if (first == false)
				{
					writer.print(","); //$NON-NLS-1$
				}
				else
				{
					first = false;
				}

				type.toSource(writer);
			}

			if (this._returnTypes.size() > 1)
			{
				writer.print(")"); //$NON-NLS-1$
			}
		}
	}
}
