/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mortbay.util.ajax.JSON.Output;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.SourcePrinter;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.js.JSTypeConstants;
import com.aptana.index.core.IndexUtil;

public class FunctionElement extends PropertyElement
{
	private static final String REFERENCES_PROPERTY = "references"; //$NON-NLS-1$
	private static final String EXCEPTIONS_PROPERTY = "exceptions"; //$NON-NLS-1$
	private static final String RETURN_TYPES_PROPERTY = "returnTypes"; //$NON-NLS-1$
	private static final String PARAMETERS_PROPERTY = "parameters"; //$NON-NLS-1$
	private static final String IS_METHOD_PROPERTY = "isMethod"; //$NON-NLS-1$
	private static final String IS_CONSTRUCTOR_PROPERTY = "isConstructor"; //$NON-NLS-1$

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
	 * FunctionElement
	 * 
	 * @param base
	 */
	public FunctionElement(FunctionElement base)
	{
		super(base);

		// NOTE: this is a shallow clone, so references are shared in lists
		this._parameters = new ArrayList<ParameterElement>(base.getParameters());
		this._references = new ArrayList<String>(base.getReferences());
		this._exceptions = new ArrayList<ExceptionElement>(base.getExceptions());
		this._returnTypes = new ArrayList<ReturnTypeElement>(base.getReturnTypes());
		this._isConstructor = base.isConstructor();
		this._isMethod = base.isMethod();
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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.contentassist.model.PropertyElement#fromJSON(java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void fromJSON(Map object)
	{
		super.fromJSON(object);

		this.setIsConstructor(Boolean.TRUE == object.get(IS_CONSTRUCTOR_PROPERTY));
		this.setIsMethod(Boolean.TRUE == object.get(IS_METHOD_PROPERTY));

		this._parameters = IndexUtil.createList(object.get(PARAMETERS_PROPERTY), ParameterElement.class);
		this._returnTypes = IndexUtil.createList(object.get(RETURN_TYPES_PROPERTY), ReturnTypeElement.class);
		this._exceptions = IndexUtil.createList(object.get(EXCEPTIONS_PROPERTY), ExceptionElement.class);
		this._references = IndexUtil.createList(object.get(REFERENCES_PROPERTY));
	}

	/**
	 * getExceptions
	 * 
	 * @return
	 */
	public List<ExceptionElement> getExceptions()
	{
		return CollectionsUtil.getListValue(this._exceptions);
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
	 * getParamterNames
	 * 
	 * @return
	 */
	public List<String> getParameterNames()
	{
		List<String> result = new ArrayList<String>();

		for (ParameterElement parameter : this.getParameters())
		{
			result.add(parameter.getName());
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
		return CollectionsUtil.getListValue(this._parameters);
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
		return CollectionsUtil.getListValue(this._references);
	}

	/**
	 * getReturnTypeNames
	 * 
	 * @return
	 */
	public List<String> getReturnTypeNames()
	{
		List<String> result = new ArrayList<String>();

		for (ReturnTypeElement type : this.getReturnTypes())
		{
			result.add(type.getType());
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
		return CollectionsUtil.getListValue(this._returnTypes);
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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.contentassist.model.PropertyElement#toJSON(org.mortbay.util.ajax.JSON.Output)
	 */
	@Override
	public void toJSON(Output out)
	{
		super.toJSON(out);

		out.add(IS_CONSTRUCTOR_PROPERTY, this.isConstructor());
		out.add(IS_METHOD_PROPERTY, this.isMethod());
		out.add(PARAMETERS_PROPERTY, this.getParameters());
		out.add(RETURN_TYPES_PROPERTY, this.getReturnTypes());
		out.add(EXCEPTIONS_PROPERTY, this.getExceptions());
		out.add(REFERENCES_PROPERTY, this.getReferences());
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
		if (this.isInstanceProperty() == false)
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
		printer.print("(").print(StringUtil.join(JSTypeConstants.PARAMETER_DELIMITER, this.getParameterTypes())).print(")"); //$NON-NLS-1$ //$NON-NLS-2$

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
