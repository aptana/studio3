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
		// NOTE: Type caching is used to prevent redundant inferencing and more
		// importantly to prevent infinite recursion with some constructs. We
		// allow empty types to generate a new array, but we don't add them to
		// it. This indicates that this object's types have been inferred. See
		// JSObject#hasTypes for more info
		if (this._types == null)
		{
			this._types = new ArrayList<String>();
		}

		if (type != null && type.length() > 0 && this._types.contains(type) == false)
		{
			this._types.add(type);
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
	 * clearTypes
	 */
	public void clearTypes()
	{
		this._types = null;
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
			result = Collections.emptyList();
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
		// NOTE: Type caching is used to prevent redundant inferencing and more
		// importantly to prevent infinite recursion with some constructs. A
		// non-null types array is all we need to consider that this object has
		// cached types so we don't the length in this predicate
		return this._types != null;
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
					printer.print("[]"); //$NON-NLS-1$
				}

				if (object.hasProperties())
				{
					printer.println(" {").increaseIndent(); //$NON-NLS-1$
					entry.getValue().toSource(printer);
					printer.decreaseIndent().printlnWithIndent("}"); //$NON-NLS-1$
				}
				else
				{
					printer.println();
				}
			}
		}
	}
}
