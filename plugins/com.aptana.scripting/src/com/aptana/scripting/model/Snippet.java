package com.aptana.scripting.model;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.jruby.anno.JRubyMethod;

import com.aptana.scope.ScopeSelector;

public class Snippet
{
	private String _path;
	private String _displayName;
	private String _trigger;
	private String _expansion;
	private String _scope;

	/**
	 * Snippet
	 * 
	 * @param name
	 */
	public Snippet(String path)
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
	 * getScope
	 * 
	 * @return
	 */
	@JRubyMethod(name = "scope")
	public String getScope()
	{
		return this._scope;
	}
	
	/**
	 * getScopeSelector
	 * 
	 * @return
	 */
	public ScopeSelector getScopeSelector()
	{
		// TODO: cache this
		return new ScopeSelector(this._scope);
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
	 * setPath
	 * 
	 * @param path
	 */
	void setPath(String path)
	{
		this._path = path;
	}
	
	/**
	 * setScope
	 * 
	 * @param scope
	 */
	@JRubyMethod(name = "scope=")
	public void setScope(String scope)
	{
		this._scope = scope;
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
		writer.append("  snippet \"").append(this._displayName).println("\" {");
		
		// show body
		writer.append("    path: ").println(this._path);
		
		// close snippet
		writer.println("  }");
		
		return sw.toString();
	}
}
