/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.inferencing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.SourcePrinter;
import com.aptana.core.util.StringUtil;
import com.aptana.js.core.model.PropertyElement;
import com.aptana.js.core.parsing.ast.JSNode;

/**
 * This class represents a single JS property, used during inferencing. Each
 */
public class JSPropertyCollection
{
	private List<JSNode> values;
	private List<String> types;
	private Map<String, JSPropertyCollection> properties;

	private String name;
	private JSPropertyCollection parentProperty;
	private PropertyElement _element;

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
		if (types == null)
		{
			types = new ArrayList<String>();
		}

		// NOTE: The number of types in the list will be small, so the contains
		// test should not have any performance issues.
		if (type != null && type.length() > 0 && !types.contains(type))
		{
			types.add(type);
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
			if (values == null)
			{
				values = new ArrayList<JSNode>();
			}

			values.add(value);
		}
	}

	/**
	 * clearTypes
	 */
	public void clearTypes()
	{
		types = null;
	}

	/**
	 * getQualifiedName
	 * 
	 * @return
	 */
	public String getQualifiedName()
	{
		List<String> parts = new ArrayList<String>();
		JSPropertyCollection current = this;

		while (current != null)
		{
			String name = current.getName();

			if (StringUtil.isEmpty(name))
			{
				break;
			}
			else
			{
				parts.add(name);
				current = current.getParentProperty();
			}
		}

		Collections.reverse(parts);

		return StringUtil.join(".", parts); //$NON-NLS-1$
	}

	/**
	 * getName
	 * 
	 * @return
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * getParentProperty
	 * 
	 * @return
	 */
	public JSPropertyCollection getParentProperty()
	{
		return parentProperty;
	}

	/**
	 * getProperty May return null
	 * 
	 * @param name
	 * @return
	 */
	public JSPropertyCollection getProperty(String name)
	{
		if (properties != null)
		{
			return properties.get(name);
		}

		return null;
	}

	/**
	 * Return a list of property names defined within this collection.
	 * 
	 * @return Returns a list of strings. This value is always defined
	 */
	public List<String> getPropertyNames()
	{
		List<String> result;

		if (properties != null)
		{
			// NOTE: We're using a LinkedHashMap to preserve the order items are
			// added to the hash. We return a list to imply the order of those
			// items...as opposed the set returned by keySet
			result = new ArrayList<String>(properties.keySet());
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
		return CollectionsUtil.getListValue(types);
	}

	/**
	 * getValues
	 * 
	 * @return
	 */
	public List<JSNode> getValues()
	{
		return CollectionsUtil.getListValue(values);
	}

	/**
	 * hasProperties
	 * 
	 * @return
	 */
	public boolean hasProperties()
	{
		return !CollectionsUtil.isEmpty(properties);
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

		if (properties != null)
		{
			result = properties.containsKey(name);
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
		return types != null;
	}

	/**
	 * setProperty
	 * 
	 * @param name
	 * @param property
	 */
	public void setProperty(String name, JSPropertyCollection property)
	{
		if (name != null && name.length() > 0 && property != null)
		{
			if (properties == null)
			{
				// Using a linked hash map to preserve order in which
				// properties were added
				properties = new LinkedHashMap<String, JSPropertyCollection>();
			}

			properties.put(name, property);

			property.name = name;
			property.parentProperty = this;
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
	private void toSource(SourcePrinter printer)
	{
		if (properties != null)
		{
			for (Map.Entry<String, JSPropertyCollection> entry : properties.entrySet())
			{
				String name = entry.getKey();
				JSPropertyCollection object = entry.getValue();

				printer.printIndent().print(name);

				if (object.values != null)
				{
					printer.print(object.values);
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

	public void setElement(PropertyElement result)
	{
		this._element = result;
	}

	public boolean hasElement()
	{
		return _element != null;
	}

	public PropertyElement getElement()
	{
		return _element;
	}
}
