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
		this.addType(returnType);
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
		return this.getTypes();
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

		buffer.append(JSTypeConstants.FUNCTION); //$NON-NLS-1$

		for (ReturnTypeElement returnType : this.getReturnTypes())
		{
			buffer.append(first ? ":" : ","); //$NON-NLS-1$ //$NON-NLS-2$
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

		if (this.isInstanceProperty())
		{
			printer.print("static ");
		}
		if (this.isInternal())
		{
			printer.print("internal ");
		}
		if (this.isConstructor())
		{
			printer.print("constructor ");
		}
		if (this.isMethod())
		{
			printer.print("method ");
		}

		printer.print(this.getName());
		printer.print("(").print(StringUtil.join(", ", this.getParameterTypes())).print(")");

		List<String> returnTypes = this.getTypeNames();
		printer.print(" : ");
		if (returnTypes != null && returnTypes.isEmpty() == false)
		{
			printer.print(StringUtil.join(",", this.getTypeNames()));
		}
		else
		{
			printer.print("void");
		}

		if (this.hasExceptions())
		{
			printer.print(" throws ").print(StringUtil.join(", ", this.getExceptionTypes()));
		}
	}
}
