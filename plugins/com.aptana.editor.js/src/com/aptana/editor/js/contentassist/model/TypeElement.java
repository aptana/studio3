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
package com.aptana.editor.js.contentassist.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.aptana.core.util.SourcePrinter;
import com.aptana.core.util.StringUtil;

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
			printer.print(" : ").print(StringUtil.join(", ", this.getParentTypes())); //$NON-NLS-1$ //$NON-NLS-2$
		}

		printer.println().print("{").increaseIndent().println(); //$NON-NLS-1$

		for (PropertyElement property : this.getProperties())
		{
			property.toSource(printer);
			printer.println(";"); //$NON-NLS-1$
		}

		printer.decreaseIndent().println("}"); //$NON-NLS-1$
	}
}
