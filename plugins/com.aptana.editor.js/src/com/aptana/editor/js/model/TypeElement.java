package com.aptana.editor.js.model;

import java.util.LinkedList;
import java.util.List;

public class TypeElement extends BaseElement
{
	private List<String> _parentTypes = new LinkedList<String>();
	private List<FunctionElement> _functions = new LinkedList<FunctionElement>();
	private List<PropertyElement> _properties = new LinkedList<PropertyElement>();

	/**
	 * TypeElement
	 */
	public TypeElement()
	{
	}

	/**
	 * addFunctionProperty
	 * 
	 * @param function
	 */
	public void addFunctionProperty(FunctionElement function)
	{
		this._functions.add(function);
		
		function.setOwningType(this);
	}

	/**
	 * addParentType
	 * 
	 * @param type
	 */
	public void addParentType(String type)
	{
		this._parentTypes.add(type);
	}

	/**
	 * addProperty
	 * 
	 * @param property
	 */
	public void addProperty(PropertyElement property)
	{
		this._properties.add(property);
		
		property.setOwningType(this);
	}

	/**
	 * getFunctionProperties
	 * 
	 * @return
	 */
	public FunctionElement[] getFunctionProperties()
	{
		return this._functions.toArray(new FunctionElement[this._functions.size()]);
	}

	/**
	 * getNonFunctionProperties
	 * 
	 * @return
	 */
	public PropertyElement[] getNonFunctionProperties()
	{
		return this._properties.toArray(new PropertyElement[this._properties.size()]);
	}

	/**
	 * getParentTypes
	 * 
	 * @return
	 */
	public String[] getParentTypes()
	{
		String[] result;
		
		if (this._parentTypes != null && this._parentTypes.size() > 0)
		{
			result = this._parentTypes.toArray(new String[this._parentTypes.size()]);
		}
		else
		{
			result = new String[] { "Object" };
		}
		
		return result;
	}
}
