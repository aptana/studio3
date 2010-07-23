package com.aptana.editor.js.contentassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.parsing.io.SourcePrinter;

public class JSObject
{
	private List<JSNode> _values;
	private List<String> _types;
	private Map<String, JSObject> _properties;

	/**
	 * addType
	 * 
	 * @param type
	 */
	public void addType(String type)
	{
		if (type != null && type.length() > 0)
		{
			if (this._types == null)
			{
				this._types = new ArrayList<String>();
			}
			
			if (this._types.contains(type) == false)
			{
				this._types.add(type);
			}
		}
	}
	
	/**
	 * addValue
	 * 
	 * @param value
	 */
	public void addValue(JSNode value)
	{
		if (value != null)
		{
			if (this._values == null)
			{
				this._values = new ArrayList<JSNode>();
			}

			this._values.add(value);
		}
	}

	/**
	 * getProperty
	 * 
	 * @param name
	 * @return
	 */
	public JSObject getProperty(String name)
	{
		JSObject result = null;

		if (this._properties != null)
		{
			result = this._properties.get(name);
		}

		return result;
	}

	/**
	 * getPropertyNames
	 * 
	 * @return
	 */
	public List<String> getPropertyNames()
	{
		List<String> result;
		
		if (this._properties != null)
		{
			result = new ArrayList<String>(this._properties.keySet());
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
	public List<String> getTypes()
	{
		List<String> result = this._types;
		
		if (result == null)
		{
			Collections.emptyList();
		}
		
		return result;
	}
	
	/**
	 * getValues
	 * 
	 * @return
	 */
	public List<JSNode> getValues()
	{
		List<JSNode> result = this._values;

		if (result == null)
		{
			result = Collections.emptyList();
		}

		return result;
	}

	/**
	 * hasProperties
	 * 
	 * @return
	 */
	public boolean hasProperties()
	{
		boolean result = false;

		if (this._properties != null)
		{
			result = this._properties.isEmpty() == false;
		}

		return result;
	}

	/**
	 * hasProperty
	 * 
	 * @param name
	 * @return
	 */
	public boolean hasProperty(String name)
	{
		boolean result = false;

		if (this._properties != null)
		{
			result = this._properties.containsKey(name);
		}

		return result;
	}

	/**
	 * hasTypes
	 * 
	 * @return
	 */
	public boolean hasTypes()
	{
		 return this._types != null && this._types.isEmpty() == false;
	}
	
	/**
	 * setProperty
	 * 
	 * @param name
	 * @param property
	 */
	public void setProperty(String name, JSObject property)
	{
		if (name != null && name.length() > 0 && property != null)
		{
			if (this._properties == null)
			{
				// Using a linked hash map to preserve order in which
				// properties were added
				this._properties = new LinkedHashMap<String, JSObject>();
			}

			this._properties.put(name, property);
		}
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
	protected void toSource(SourcePrinter printer)
	{
		if (this._properties != null)
		{
			for (Map.Entry<String, JSObject> entry : this._properties.entrySet())
			{
				String name = entry.getKey();
				JSObject object = entry.getValue();

				printer.printIndent().print(name);

				if (object._values != null)
				{
					printer.print(object._values);
				}
				else
				{
					printer.print("[]");
				}

				if (object.hasProperties())
				{
					printer.println(" {").increaseIndent();
					entry.getValue().toSource(printer);
					printer.decreaseIndent().printlnWithIndent("}");
				}
				else
				{
					printer.println();
				}
			}
		}
	}
}
