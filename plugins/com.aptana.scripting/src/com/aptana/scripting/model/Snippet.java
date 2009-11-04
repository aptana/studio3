package com.aptana.scripting.model;

import org.jruby.anno.JRubyMethod;

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
}
