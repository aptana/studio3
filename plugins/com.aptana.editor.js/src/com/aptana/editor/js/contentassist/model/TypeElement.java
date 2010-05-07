package com.aptana.editor.js.contentassist.model;

import java.util.LinkedList;
import java.util.List;

public class TypeElement extends BaseElement
{
	private List<String> _parentTypes = new LinkedList<String>();
	private List<PropertyElement> _properties = new LinkedList<PropertyElement>();

	/**
	 * TypeElement
	 */
	public TypeElement()
	{
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
	 * getProperties
	 * 
	 * @return
	 */
	public PropertyElement[] getProperties()
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
			result = new String[] { "Object" }; //$NON-NLS-1$
		}
		
		return result;
	}
}
