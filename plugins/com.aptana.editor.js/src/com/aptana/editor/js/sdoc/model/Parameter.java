package com.aptana.editor.js.sdoc.model;

import beaver.Symbol;

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
}
