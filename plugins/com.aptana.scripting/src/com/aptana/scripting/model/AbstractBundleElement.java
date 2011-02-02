/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import com.aptana.core.util.StringUtil;
import com.aptana.scope.IScopeSelector;
import com.aptana.scope.MatchAnyScopeSelector;
import com.aptana.scope.ScopeSelector;

public abstract class AbstractBundleElement extends AbstractElement
{
	private static final String ALL_SCOPES = "all"; //$NON-NLS-1$

	private String _scope;
	private IScopeSelector _scopeSelector;
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
	public IScopeSelector getScopeSelector()
	{
		if (this._scopeSelector == null)
		{
			if (this._scope == null || this._scope.length() == 0)
			{
				this._scopeSelector = new MatchAnyScopeSelector();
			}
			else
			{
				this._scopeSelector = new ScopeSelector(this._scope);
			}
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
		IScopeSelector selector = this.getScopeSelector();
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
		IScopeSelector selector = this.getScopeSelector();
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
