/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
