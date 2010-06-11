package com.aptana.editor.js.sdoc.model;

import java.util.LinkedList;
import java.util.List;

public class FunctionType extends Type
{
	private List<Type> _parameterTypes = new LinkedList<Type>();
	private List<Type> _returnTypes = new LinkedList<Type>();
	
	/**
	 * FunctionType
	 */
	public FunctionType()
	{
		super("Function");
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
}
