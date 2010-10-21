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

import com.aptana.core.util.StringUtil;
import com.aptana.editor.js.JSTypeConstants;
import com.aptana.parsing.io.SourcePrinter;

public class FunctionElement extends PropertyElement
{
	private List<ParameterElement> _parameters;
	private List<String> _references;
	private List<ExceptionElement> _exceptions;
	private List<ReturnTypeElement> _returnTypes;

	private boolean _isConstructor;
	private boolean _isMethod;

	/**
	 * FunctionElement
	 */
	public FunctionElement()
	{
	}

	/**
	 * addException
	 * 
	 * @param currentException
	 */
	public void addException(ExceptionElement exception)
	{
		if (exception != null)
		{
			if (this._exceptions == null)
			{
				this._exceptions = new ArrayList<ExceptionElement>();
			}

			this._exceptions.add(exception);
		}
	}

	/**
	 * addParameter
	 * 
	 * @param parameter
	 */
	public void addParameter(ParameterElement parameter)
	{
		if (parameter != null)
		{
			if (this._parameters == null)
			{
				this._parameters = new ArrayList<ParameterElement>();
			}

			this._parameters.add(parameter);
		}
	}

	/**
	 * addReferences
	 * 
	 * @param reference
	 */
	public void addReference(String reference)
	{
		if (reference != null && reference.length() > 0)
		{
			if (this._references == null)
			{
				this._references = new ArrayList<String>();
			}

			this._references.add(reference);
		}
	}

	/**
	 * addReturnType
	 * 
	 * @param returnType
	 */
	public void addReturnType(ReturnTypeElement returnType)
	{
		if (returnType != null)
		{
			if (this._returnTypes == null)
			{
				this._returnTypes = new ArrayList<ReturnTypeElement>();
			}

			int index = this._returnTypes.indexOf(returnType);

			if (index != -1)
			{
				this._returnTypes.set(index, returnType);
			}
			else
			{
				this._returnTypes.add(returnType);
			}
		}
	}

	/**
	 * addReturnType
	 * 
	 * @param type
	 */
	public void addReturnType(String type)
	{
		if (type != null && type.length() > 0)
		{
			ReturnTypeElement returnType = new ReturnTypeElement();

			returnType.setType(type);

			this.addReturnType(returnType);
		}
	}

	/**
	 * getExceptions
	 * 
	 * @return
	 */
	public List<ExceptionElement> getExceptions()
	{
		List<ExceptionElement> result = this._exceptions;

		if (result == null)
		{
			result = Collections.emptyList();
		}

		return result;
	}

	/**
	 * getExceptionTypes
	 * 
	 * @return
	 */
	public List<String> getExceptionTypes()
	{
		List<String> result = new ArrayList<String>();

		for (ExceptionElement exception : this.getExceptions())
		{
			result.add(exception.getType());
		}

		return result;
	}

	/**
	 * getParameters
	 * 
	 * @return
	 */
	public List<ParameterElement> getParameters()
	{
		List<ParameterElement> result = this._parameters;

		if (result == null)
		{
			result = Collections.emptyList();
		}

		return result;
	}

	/**
	 * getParameterTypes
	 * 
	 * @return
	 */
	public List<String> getParameterTypes()
	{
		List<String> result = new ArrayList<String>();

		for (ParameterElement parameter : this.getParameters())
		{
			result.add(StringUtil.join(JSTypeConstants.PARAMETER_TYPE_DELIMITER, parameter.getTypes()));
		}

		return result;
	}

	/**
	 * getReferences
	 * 
	 * @return
	 */
	public List<String> getReferences()
	{
		List<String> result = this._references;

		if (result == null)
		{
			result = Collections.emptyList();
		}

		return result;
	}

	/**
	 * getReturnTypes
	 * 
	 * @return
	 */
	public List<ReturnTypeElement> getReturnTypes()
	{
		List<ReturnTypeElement> result = this._returnTypes;

		if (result == null)
		{
			result = Collections.emptyList();
		}

		return result;
	}

	/**
	 * getReturnTypeNames
	 * 
	 * @return
	 */
	public List<String> getReturnTypeNames()
	{
		List<String> result;

		if (this._returnTypes != null)
		{
			result = new ArrayList<String>(this._returnTypes.size());

			for (ReturnTypeElement type : this._returnTypes)
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
	 * getSignature
	 * 
	 * @return
	 */
	public String getSignature()
	{
		StringBuilder buffer = new StringBuilder();
		boolean first = true;

		// include actual type in custom notation, if not a function
		List<String> types = this.getTypeNames();

		if (types != null && types.size() > 0)
		{
			buffer.append(StringUtil.join(JSTypeConstants.RETURN_TYPE_DELIMITER, types));
		}
		else
		{
			buffer.append(JSTypeConstants.FUNCTION_TYPE);
		}

		// include return types
		for (ReturnTypeElement returnType : this.getReturnTypes())
		{
			buffer.append(first ? JSTypeConstants.FUNCTION_SIGNATURE_DELIMITER : JSTypeConstants.RETURN_TYPE_DELIMITER);
			buffer.append(returnType.getType());
			first = false;
		}

		return buffer.toString();
	}

	/**
	 * hasExceptions
	 * 
	 * @return
	 */
	public boolean hasExceptions()
	{
		return this._exceptions != null && this._exceptions.isEmpty() == false;
	}

	/**
	 * hasParameters
	 * 
	 * @return
	 */
	public boolean hasParameters()
	{
		return this._parameters != null && this._parameters.isEmpty() == false;
	}

	/**
	 * isConstructor
	 * 
	 * @return
	 */
	public boolean isConstructor()
	{
		return this._isConstructor;
	}

	/**
	 * isMethod
	 * 
	 * @return
	 */
	public boolean isMethod()
	{
		return this._isMethod;
	}

	/**
	 * setIsConstructor
	 * 
	 * @param value
	 */
	public void setIsConstructor(boolean value)
	{
		this._isConstructor = value;
	}

	/**
	 * setIsMethod
	 * 
	 * @param value
	 */
	public void setIsMethod(boolean value)
	{
		this._isMethod = value;
	}

	/**
	 * toSource
	 * 
	 * @param printer
	 */
	public void toSource(SourcePrinter printer)
	{
		printer.printIndent();

		// print any annotations
		if (this.isInstanceProperty())
		{
			printer.print("static "); //$NON-NLS-1$
		}
		if (this.isInternal())
		{
			printer.print("internal "); //$NON-NLS-1$
		}
		if (this.isConstructor())
		{
			printer.print("constructor "); //$NON-NLS-1$
		}
		if (this.isMethod())
		{
			printer.print("method "); //$NON-NLS-1$
		}

		// print name
		printer.print(this.getName());

		// print parameter types
		printer.print("(").print(StringUtil.join(JSTypeConstants.PARAMETER_TYPE_DELIMITER, this.getParameterTypes())).print(")"); //$NON-NLS-1$ //$NON-NLS-2$

		// print return types
		List<String> returnTypes = this.getReturnTypeNames();

		printer.print(JSTypeConstants.FUNCTION_SIGNATURE_DELIMITER);

		if (returnTypes != null && returnTypes.isEmpty() == false)
		{
			printer.print(StringUtil.join(JSTypeConstants.RETURN_TYPE_DELIMITER, returnTypes));
		}
		else
		{
			printer.print(JSTypeConstants.UNDEFINED_TYPE);
		}

		// print exceptions
		if (this.hasExceptions())
		{
			printer.print(" throws ").print(StringUtil.join(", ", this.getExceptionTypes())); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
}
