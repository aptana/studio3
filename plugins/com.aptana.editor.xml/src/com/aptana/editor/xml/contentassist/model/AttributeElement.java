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
package com.aptana.editor.xml.contentassist.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AttributeElement
{
	private static final String EMPTY = ""; //$NON-NLS-1$

	private String _name;
	private String _element;
	private String _description;
	private List<ValueElement> _values;

	/**
	 * AttributeElement
	 */
	public AttributeElement()
	{
	}

	/**
	 * addValue
	 * 
	 * @param values
	 *            the value to add
	 */
	public void addValue(ValueElement value)
	{
		if (this._values == null)
		{
			this._values = new ArrayList<ValueElement>();
		}

		this._values.add(value);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		boolean result = false;

		if (obj instanceof AttributeElement)
		{
			AttributeElement that = (AttributeElement) obj;

			result = //
			this.getName().equals(that.getName()) //
				&& this.getElement().equals(that.getElement()) //
				&& this.getDescription().equals(that.getDescription()) //
				&& this.getValues().equals(that.getValues());
		}

		return result;
	}

	/**
	 * getDescription
	 * 
	 * @return the description
	 */
	public String getDescription()
	{
		return (this._description == null) ? EMPTY : this._description;
	}

	/**
	 * getElement
	 * 
	 * @return
	 */
	public String getElement()
	{
		return (this._element == null) ? EMPTY : this._element;
	}

	/**
	 * getName
	 * 
	 * @return the name
	 */
	public String getName()
	{
		return (this._name == null) ? EMPTY : this._name;
	}

	/**
	 * getValues
	 * 
	 * @return the values
	 */
	public List<ValueElement> getValues()
	{
		List<ValueElement> result = Collections.emptyList();

		if (this._values != null)
		{
			result = this._values;
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int h = this.getName().hashCode();

		h = 31 * h + this.getElement().hashCode();
		h = 31 * h + this.getDescription().hashCode();
		h = 31 * h + this.getValues().hashCode();

		return h;
	}

	/**
	 * setDescription
	 * 
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description)
	{
		this._description = description;
	}

	/**
	 * setElement
	 * 
	 * @param element
	 */
	public void setElement(String element)
	{
		this._element = element;
	}

	/**
	 * setName
	 * 
	 * @param name
	 *            the name to set
	 */
	public void setName(String name)
	{
		this._name = name;
	}
}
