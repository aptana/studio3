package com.aptana.scripting.model;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.jruby.anno.JRubyMethod;

import com.aptana.scope.ScopeSelector;

public class Menu
{
	private String _path;
	private String _displayName;
	private String _scope;

	/**
	 * Snippet
	 * 
	 * @param path
	 */
	public Menu(String path)
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
	 * setScope
	 * 
	 * @param scope
	 */
	@JRubyMethod(name = "scope=")
	public void setScope(String scope)
	{
		this._scope = scope;
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
		writer.append("  menu \"").append(this._displayName).println("\" {"); //$NON-NLS-1$ //$NON-NLS-2$
		
		// show body
		writer.append("    path:  ").println(this._path); //$NON-NLS-1$
		writer.append("    scope: ").println(this._scope); //$NON-NLS-1$
		
		// close snippet
		writer.println("  }"); //$NON-NLS-1$
		
		return sw.toString();
	}
}
