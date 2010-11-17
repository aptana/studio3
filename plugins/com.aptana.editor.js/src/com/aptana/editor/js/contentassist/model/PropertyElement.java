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
import com.aptana.editor.js.JSTypeConstants;

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

			int index = this._types.indexOf(type);

			if (index != -1)
			{
				this._types.set(index, type);
			}
			else
			{
				this._types.add(type);
			}
		}
	}

	/**
	 * addType
	 * 
	 * @param type
	 */
	public void addType(String type)
	{
		if (type != null && type.length() > 0)
		{
			ReturnTypeElement returnType = new ReturnTypeElement();

			returnType.setType(type);

			this.addType(returnType);
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
			printer.print("static "); //$NON-NLS-1$
		}
		if (this.isInternal())
		{
			printer.print("internal "); //$NON-NLS-1$
		}

		printer.print(this.getName());
		printer.print(" : "); //$NON-NLS-1$

		List<String> types = this.getTypeNames();

		if (types != null && types.size() > 0)
		{
			printer.print(StringUtil.join(",", this.getTypeNames())); //$NON-NLS-1$
		}
		else
		{
			printer.print(JSTypeConstants.UNDEFINED_TYPE);
		}
	}
}
