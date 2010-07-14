package com.aptana.editor.js.contentassist.model;

import java.util.ArrayList;
import java.util.List;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.js.JSTypes;
import com.aptana.parsing.io.SourcePrinter;

public class FunctionElement extends PropertyElement
{
	private List<ParameterElement> _parameters = new ArrayList<ParameterElement>();
	private List<String> _references = new ArrayList<String>();
	private List<ExceptionElement> _exceptions = new ArrayList<ExceptionElement>();

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
		this._exceptions.add(exception);
	}

	/**
	 * addParameter
	 * 
	 * @param parameter
	 */
	public void addParameter(ParameterElement parameter)
	{
		this._parameters.add(parameter);
	}

	/**
	 * addReferences
	 * 
	 * @param reference
	 */
	public void addReference(String reference)
	{
		this._references.add(reference);
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
		return this._exceptions;
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
		return this._parameters;
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
			result.add(StringUtil.join("|", parameter.getTypes()));
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
		return this._references;
	}

	/**
	 * getReturnTypes
	 * 
	 * @return
	 */
	public ReturnTypeElement[] getReturnTypes()
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

		buffer.append(JSTypes.FUNCTION); //$NON-NLS-1$

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
		return this._exceptions.isEmpty() == false;
	}

	/**
	 * hasParameters
	 * 
	 * @return
	 */
	public boolean hasParameters()
	{
		return this._parameters.isEmpty() == false;
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

		if (this.hasParameters())
		{
			printer.print("(").print(StringUtil.join(", ", this.getParameterTypes())).print(")");
		}

		printer.print(" : ");
		printer.print(StringUtil.join(",", this.getTypeNames()));
		
		if (this.hasExceptions())
		{
			printer.print(" throws ").print(StringUtil.join(", ", this.getExceptionTypes()));
		}
		
		printer.println(";");
	}
}
