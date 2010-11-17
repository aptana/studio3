/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.inferencing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.aptana.core.util.SourcePrinter;
import com.aptana.editor.js.parsing.ast.JSNode;

public class JSPropertyCollection
{
	private List<JSNode> _values;
	private List<String> _types;
	private Map<String, JSPropertyCollection> _properties;

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

		// NOTE: The number of types in the list will be small, so the contains
		// test should not have any performance issues.
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
	public JSPropertyCollection getProperty(String name)
	{
		JSPropertyCollection result = null;

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
			// NOTE: We're using a LinkedHashMap to preserve the order items are
			// added to the hash. We return a list to imply the order of those
			// items...as opposed the set returned by keySet
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
	public void setProperty(String name, JSPropertyCollection property)
	{
		if (name != null && name.length() > 0 && property != null)
		{
			if (this._properties == null)
			{
				// Using a linked hash map to preserve order in which
				// properties were added
				this._properties = new LinkedHashMap<String, JSPropertyCollection>();
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
			for (Map.Entry<String, JSPropertyCollection> entry : this._properties.entrySet())
			{
				String name = entry.getKey();
				JSPropertyCollection object = entry.getValue();

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
