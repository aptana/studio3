package com.aptana.editor.js.contentassist.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.aptana.core.util.StringUtil;
import com.aptana.parsing.io.SourcePrinter;

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
		if (this._parentTypes.contains(type) == false)
		{
			this._parentTypes.add(type);
		}
	}

	/**
	 * addProperty
	 * 
	 * @param property
	 */
	public void addProperty(PropertyElement property)
	{
		if (property != null)
		{
			int index = this.getPropertyIndex(property.getName());
			
			if (index >= 0)
			{
				// replace existing property with the same name
				this._properties.set(index, property);
			}
			else
			{
				// add to the end of our list
				this._properties.add(property);
			}
			
			property.setOwningType(this);
		}
	}

	/**
	 * getProperties
	 * 
	 * @return
	 */
	public List<PropertyElement> getProperties()
	{
		return this._properties;
	}

	/**
	 * getProperty
	 * 
	 * @param name
	 * @return
	 */
	public PropertyElement getProperty(String name)
	{
		int index = this.getPropertyIndex(name);
		PropertyElement result = null;
		
		if (index != -1)
		{
			result = this._properties.get(index);
		}
		
		return result;
	}
	
	/**
	 * getPropertyIndex
	 * 
	 * @param name
	 * @return
	 */
	protected int getPropertyIndex(String name)
	{
		int result = -1;
		
		if (name != null && name.length() > 0)
		{
			for (int i = 0; i < this._properties.size(); i++)
			{
				PropertyElement property = this._properties.get(i);
				
				if (name.equals(property.getName()))
				{
					result = i;
					break;
				}
			}
		}
		
		return result;
	}
	
	/**
	 * getParentTypes
	 * 
	 * @return
	 */
	public List<String> getParentTypes()
	{
		if (this._parentTypes == null)
		{
			this._parentTypes = Collections.emptyList();
		}

		return this._parentTypes;
	}
	
	/**
	 * hasParentTypes
	 * 
	 * @return
	 */
	public boolean hasParentTypes()
	{
		return this._parentTypes.isEmpty() == false;
	}
	
	/**
	 * hasProperties
	 * 
	 * @return
	 */
	public boolean hasProperties()
	{
		return this._properties.isEmpty() == false;
	}
	
	/**
	 * toSource
	 * 
	 * @return
	 */
	public String toSource()
	{
		SourcePrinter printer = new SourcePrinter();
		
		this.toSource(printer);
		
		return printer.toString();
	}
	
	/**
	 * toSource
	 * 
	 * @param printer
	 */
	public void toSource(SourcePrinter printer)
	{
		printer.print(this.getName());
		
		if (this.hasParentTypes())
		{
			printer.print(" : ").print(StringUtil.join(", ", this.getParentTypes()));
		}
		
		printer.println().print("{").increaseIndent().println();
		
		for (PropertyElement property : this.getProperties())
		{
			property.toSource(printer);
		}
		
		printer.decreaseIndent().println("}");
	}
}
