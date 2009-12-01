package com.aptana.scripting.model;

import org.jruby.anno.JRubyMethod;

public class Snippet extends AbstractModel
{
	private String _trigger;
	private String _expansion;

	/**
	 * Snippet
	 * 
	 * @param name
	 */
	public Snippet(String path)
	{
		super(path);
	}

	/**
	 * getExpansion
	 * 
	 * @return
	 */
	@JRubyMethod(name = "expansion")
	public String getExpansion()
	{
		return this._expansion;
	}
	
	/**
	 * getTrigger
	 * 
	 * @return
	 */
	@JRubyMethod(name = "trigger")
	public String getTrigger()
	{
		return this._trigger;
	}
	
	/**
	 * setExpansion
	 * 
	 * @param expansion
	 */
	@JRubyMethod(name = "expansion=")
	public void setExpansion(String expansion)
	{
		this._expansion = expansion;
	}

	/**
	 * setTrigger
	 * 
	 * @param trigger
	 */
	@JRubyMethod(name = "trigger=")
	public void setTrigger(String trigger)
	{
		this._trigger = trigger;
	}
	
	/**
	 * toSource
	 */
	protected void toSource(SourcePrinter printer)
	{
		printer.printWithIndent("snippet \"").print(this._displayName).println("\" {").increaseIndent(); //$NON-NLS-1$ //$NON-NLS-2$
		
		printer.printWithIndent("path: ").println(this._path); //$NON-NLS-1$
		printer.printWithIndent("scope: ").println(this._scope); //$NON-NLS-1$
		printer.printWithIndent("trigger: ").println(this._trigger); //$NON-NLS-1$
		
		printer.decreaseIndent().printlnWithIndent("}");
	}
}
