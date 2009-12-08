package com.aptana.scripting.model;

import org.jruby.anno.JRubyMethod;

import com.aptana.scope.ScopeSelector;

public abstract class AbstractBundleElement extends AbstractElement
{
	protected BundleElement _owningBundle;
	protected String _scope;

	/**
	 * AbstractBundleElement
	 * 
	 * @param path
	 */
	public AbstractBundleElement(String path)
	{
		super(path);
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
	 * setOwningBundle
	 * 
	 * @param bundle
	 */
	void setOwningBundle(BundleElement bundle)
	{
		this._owningBundle = bundle;
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
}
