package com.aptana.editor.js.contentassist.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.aptana.core.util.StringUtil;
import com.aptana.parsing.io.SourcePrinter;

public class PropertyElement extends BaseElement
{
	private String _owningType;
	private boolean _isInstanceProperty;
	private boolean _isClassProperty;
	private boolean _isInternal;
	private List<ReturnTypeElement> _types;
	private List<String> _examples;

	/**
	 * PropertyElement
	 */
	public PropertyElement()
	{
	}

	/**
	 * addExample
	 * 
	 * @param example
	 */
	public void addExample(String example)
	{
		if (example != null && example.length() > 0)
		{
			if (this._examples == null)
			{
				this._examples = new ArrayList<String>();
			}

			this._examples.add(example);
		}
	}

	/**
	 * addType
	 * 
	 * @param type
	 */
	public void addType(ReturnTypeElement type)
	{
		if (type != null)
		{
			if (this._types == null)
			{
				this._types = new ArrayList<ReturnTypeElement>();
			}

			this._types.add(type);
		}
	}

	/**
	 * getExamples
	 * 
	 * @return
	 */
	public List<String> getExamples()
	{
		List<String> result = this._examples;

		if (result == null)
		{
			result = Collections.emptyList();
		}

		return result;
	}

	/**
	 * getOwningType
	 * 
	 * @return
	 */
	public String getOwningType()
	{
		return this._owningType;
	}

	/**
	 * getTypeNames
	 * 
	 * @return
	 */
	public List<String> getTypeNames()
	{
		List<String> result;

		if (this._types != null)
		{
			result = new ArrayList<String>(this._types.size());

			for (ReturnTypeElement type : this._types)
			{
				result.add(type.getType());
			}
		}
		else
		{
			result = Collections.emptyList();
		}

		return result;
	}

	/**
	 * getTypes
	 * 
	 * @return
	 */
	public List<ReturnTypeElement> getTypes()
	{
		List<ReturnTypeElement> result = this._types;

		if (result == null)
		{
			result = Collections.emptyList();
		}

		return result;
	}

	/**
	 * isClassProperty
	 * 
	 * @return
	 */
	public boolean isClassProperty()
	{
		return this._isClassProperty;
	}

	/**
	 * isInstanceProperty
	 * 
	 * @return
	 */
	public boolean isInstanceProperty()
	{
		return this._isInstanceProperty;
	}

	/**
	 * isInternal
	 * 
	 * @return
	 */
	public boolean isInternal()
	{
		return this._isInternal;
	}

	/**
	 * setIsClassProperty
	 * 
	 * @param value
	 */
	public void setIsClassProperty(boolean value)
	{
		this._isClassProperty = value;
	}

	/**
	 * setIsInstanceProperty
	 * 
	 * @param value
	 */
	public void setIsInstanceProperty(boolean value)
	{
		this._isInstanceProperty = value;
	}

	/**
	 * setIsInternal
	 * 
	 * @param value
	 */
	public void setIsInternal(boolean value)
	{
		this._isInternal = value;
	}

	/**
	 * setOwningType
	 * 
	 * @param type
	 */
	public void setOwningType(String type)
	{
		this._owningType = type;
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
		printer.printIndent();

		if (this.isInstanceProperty())
		{
			printer.print("static ");
		}
		if (this.isInternal())
		{
			printer.print("internal ");
		}

		printer.print(this.getName());
		printer.print(" : ");

		List<String> types = this.getTypeNames();

		if (types != null && types.size() > 0)
		{
			printer.print(StringUtil.join(",", this.getTypeNames()));
		}
		else
		{
			printer.print("undefined");
		}
	}
}
