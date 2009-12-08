package com.aptana.scripting.model;

import org.jruby.anno.JRubyMethod;

public abstract class AbstractElement
{
	protected String _path;
	protected String _displayName;

	/**
	 * ModelBase
	 * 
	 * @param path
	 */
	public AbstractElement(String path)
	{
		this._path = path;
	}

	/**
	 * getDisplayName
	 * 
	 * @return
	 */
	@JRubyMethod(name = "display_name")
	public String getDisplayName()
	{
		return this._displayName;
	}

	/**
	 * getPath
	 * 
	 * @return
	 */
	@JRubyMethod(name = "path")
	public String getPath()
	{
		return this._path;
	}

	/**
	 * setDisplayName
	 * 
	 * @param displayName
	 */
	@JRubyMethod(name = "display_name=")
	public void setDisplayName(String displayName)
	{
		this._displayName = displayName;
	}

	/**
	 * setPath
	 * 
	 * @param path
	 */
	void setPath(String path)
	{
		this._path = path;
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
	abstract protected void toSource(SourcePrinter printer);
}