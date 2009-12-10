package com.aptana.scripting.model;

import com.aptana.scope.ScopeSelector;

public abstract class AbstractBundleElement extends AbstractElement
{
	protected BundleElement _owningBundle;
	protected String _scope;
	protected ScopeSelector _scopeSelector;

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
		if (this._scopeSelector == null)
		{
			this._scopeSelector = new ScopeSelector(this._scope);
		}
		
		return this._scopeSelector;
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
	public void setScope(String scope)
	{
		// TODO: only reset scope selector is scope actually changes
		this._scope = scope;
		this._scopeSelector = null;
	}
}
