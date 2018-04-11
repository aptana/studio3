/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.parsing.sdoc.model;

import beaver.Symbol;

import com.aptana.core.util.SourcePrinter;

public class Parameter extends Symbol
{
	private String _name;
	private Usage _usage;

	/**
	 * Parameter
	 * 
	 * @param name
	 */
	public Parameter(String name)
	{
		this._name = name;
		this._usage = Usage.REQUIRED;
	}

	/**
	 * getName
	 * 
	 * @return
	 */
	public String getName()
	{
		return this._name;
	}

	/**
	 * getUsage
	 * 
	 * @return
	 */
	public Usage getUsage()
	{
		return this._usage;
	}

	/**
	 * setUsage
	 * 
	 * @param usage
	 */
	public void setUsage(Usage usage)
	{
		this._usage = usage;
	}

	/**
	 * toSource
	 * 
	 * @return
	 */
	public String toSource()
	{
		SourcePrinter writer = new SourcePrinter();

		this.toSource(writer);

		return writer.toString();
	}

	/**
	 * toSource
	 * 
	 * @param writer
	 */
	public void toSource(SourcePrinter writer)
	{
		switch (this._usage)
		{
			case REQUIRED:
				writer.print(this._name);
				break;

			case OPTIONAL:
				writer.print('[').print(this._name).print(']');
				break;

			case ONE_OR_MORE:
				writer.print("..."); //$NON-NLS-1$
				break;

			case ZERO_OR_MORE:
				writer.print("[...]"); //$NON-NLS-1$
				break;
		}
	}
}
