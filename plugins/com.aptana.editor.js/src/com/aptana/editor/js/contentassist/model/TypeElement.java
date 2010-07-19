package com.aptana.editor.js.contentassist.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.aptana.core.util.StringUtil;
import com.aptana.parsing.io.SourcePrinter;

public class TypeElement extends BaseElement
{
	private List<String> _parentTypes;
	private List<PropertyElement> _properties;

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
		if (type != null && type.length() > 0)
		{
			if (this._parentTypes == null)
			{
				this._parentTypes = new ArrayList<String>();
			}

			if (this._parentTypes.contains(type) == false)
			{
				this._parentTypes.add(type);
			}
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
			if (this._properties == null)
			{
				this._properties = new ArrayList<PropertyElement>();
			}

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

			property.setOwningType(this.getName());
		}
	}

	/**
	 * getProperties
	 * 
	 * @return
	 */
	public List<PropertyElement> getProperties()
	{
		List<PropertyElement> result = this._properties;

		if (result == null)
		{
			result = Collections.emptyList();
		}

		return result;
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

		if (name != null && name.length() > 0 && this._properties != null)
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
		List<String> result = this._parentTypes;

		if (result == null)
		{
			result = Collections.emptyList();
		}

		return result;
	}

	/**
	 * hasParentTypes
	 * 
	 * @return
	 */
	public boolean hasParentTypes()
	{
		return this._parentTypes != null && this._parentTypes.isEmpty() == false;
	}

	/**
	 * hasProperties
	 * 
	 * @return
	 */
	public boolean hasProperties()
	{
		return this._properties != null && this._properties.isEmpty() == false;
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
			printer.println(";");
		}

		printer.decreaseIndent().println("}");
	}
}
