package com.aptana.scripting.model;

import java.io.PrintWriter;
import java.io.StringWriter;

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
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		
		// open snippet
		writer.append("  snippet \"").append(this._displayName).println("\" {"); //$NON-NLS-1$ //$NON-NLS-2$
		
		// show body
		writer.append("    path:  ").println(this._path); //$NON-NLS-1$
		writer.append("    scope: ").println(this._scope); //$NON-NLS-1$
		writer.append("    trigger: ").println(this._trigger); //$NON-NLS-1$
		
		// close snippet
		writer.println("  }"); //$NON-NLS-1$
		
		return sw.toString();
	}
}
