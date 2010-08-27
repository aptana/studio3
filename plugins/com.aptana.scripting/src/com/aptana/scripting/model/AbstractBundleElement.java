package com.aptana.scripting.model;

import com.aptana.core.util.StringUtil;
import com.aptana.scope.ScopeSelector;

public abstract class AbstractBundleElement extends AbstractElement
{
	private static final String ALL_SCOPES = "all"; //$NON-NLS-1$
	
	private String _scope;
	private ScopeSelector _scopeSelector;
	protected BundleElement owningBundle;

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
		return this.owningBundle;
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
		if (this._scopeSelector == null && this._scope != null && this._scope.length() > 0)
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
		ScopeSelector selector = this.getScopeSelector();
		boolean result = true;
		
		if (selector != null)
		{
			result = selector.matches(scope);
		}
		
		return result;
	}
	
	/**
	 * matches
	 * 
	 * @param scopes
	 * @return
	 */
	public boolean matches(String[] scopes)
	{
		ScopeSelector selector = this.getScopeSelector();
		boolean result = true;
		
		if (selector != null)
		{
			result = selector.matches(scopes);
		}
		
		return result;
	}
	
	/**
	 * setOwningBundle
	 * 
	 * @param bundle
	 */
	void setOwningBundle(BundleElement bundle)
	{
		this.owningBundle = bundle;
	}

	/**
	 * setScope
	 * 
	 * @param scope
	 */
	public void setScope(String scope)
	{
		// NOTE: If a scope selector is null, then we match
		// any scope. We convert "all" to null since they
		// are equivalent
		if (scope != null && scope.equals(ALL_SCOPES))
		{
			scope = null;
		}
		
		if (StringUtil.areNotEqual(this._scope, scope))
		{
			this._scope = scope;
			this._scopeSelector = null;
		}
	}
}
