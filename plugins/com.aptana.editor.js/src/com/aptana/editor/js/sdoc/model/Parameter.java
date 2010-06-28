package com.aptana.editor.js.sdoc.model;

import com.aptana.parsing.io.SourceWriter;

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
	
	/**
	 * toSource
	 * 
	 * @return
	 */
	public String toSource()
	{
		SourceWriter writer = new SourceWriter();
		
		this.toSource(writer);
		
		return writer.toString();
	}
	
	/**
	 * toSource
	 * 
	 * @param writer
	 */
	public void toSource(SourceWriter writer)
	{
		switch (this._usage)
		{
			case REQUIRED:
				writer.print(this._name);
				break;
				
			case OPTIONAL:
				writer.print("[").print(this._name).print("]");
				break;
				
			case ONE_OR_MORE:
				writer.print("...");
				break;
				
			case ZERO_OR_MORE:
				writer.print("[...]");
				break;
		}
	}
}
