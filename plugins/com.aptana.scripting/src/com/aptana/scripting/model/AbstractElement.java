package com.aptana.scripting.model;

import org.jruby.anno.JRubyMethod;

import com.aptana.scope.ScopeSelector;

public abstract class AbstractElement
{
	protected BundleElement _owningBundle;
	protected String _path;
	protected String _displayName;
	protected String _scope;

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
	 * getOwningBundle
	 * 
	 * @return
	 */
	public BundleElement getOwningBundle()
	{
		return this._owningBundle;
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
	 * matches
	 * 
	 * @param scope
	 * @return
	 */
	public boolean matches(String scope)
	{
		return this.getScopeSelector().matches(scope);
	}
	
	/**
	 * matches
	 * 
	 * @param scopes
	 * @return
	 */
	public boolean matches(String[] scopes)
	{
		return this.getScopeSelector().matches(scopes);
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
	 * setOwningBundle
	 * 
	 * @param bundle
	 */
	void setOwningBundle(BundleElement bundle)
	{
		this._owningBundle = bundle;
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