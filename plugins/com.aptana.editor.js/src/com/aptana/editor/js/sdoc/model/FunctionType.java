package com.aptana.editor.js.sdoc.model;

import java.util.LinkedList;
import java.util.List;

import com.aptana.editor.js.JSTypes;
import com.aptana.parsing.io.SourcePrinter;

public class FunctionType extends Type
{
	private List<Type> _parameterTypes = new LinkedList<Type>();
	private List<Type> _returnTypes = new LinkedList<Type>();

	/**
	 * FunctionType
	 */
	public FunctionType()
	{
		super(JSTypes.FUNCTION); //$NON-NLS-1$
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
		return this._returnTypes;
	}

	/**
	 * toSource
	 * 
	 * @param writer
	 */
	public void toSource(SourcePrinter writer)
	{
		writer.print(JSTypes.FUNCTION); //$NON-NLS-1$

		boolean first;

		if (this._parameterTypes.isEmpty() == false)
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

		if (this._returnTypes.isEmpty() == false)
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
